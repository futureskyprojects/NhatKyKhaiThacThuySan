package vn.vistark.nkktts.core.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.db.TripWaitForSync
import vn.vistark.nkktts.ui.khoi_dong.ManHinhKhoiDong
import java.lang.Exception
import java.util.*

class SyncService : Service() {
    // Phần khai báo liên quan đến thông báo (Notification)
    private val mNotificationChannelId = "setting"
    private val mNotificationId = 123
    lateinit var mHandler: Handler
    lateinit var notification: Notification
    lateinit var notiManager: NotificationManager

    var notiType = ""
    var previousNumber = -1;

    protected fun onHandleIntent(msg: String) {
        mHandler.post {
            Toast.makeText(
                this,
                msg,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private lateinit var timer: Timer


    override fun onCreate() {
        super.onCreate()
        mHandler = Handler()
        notiManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // A. Tạo notification channel cho android phiên bản từ O đổ lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    mNotificationChannelId,
                    "Cài đặt",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            notiManager.createNotificationChannel(channel)
        }

        defaultNotification()

        // C. Tiến hành chạy Forefround (chạy dưới nền)
        startForeground(mNotificationId, notification)

        // Thực hiện các tác vụ tại đây
        sync()
    }

    private fun defaultNotification() {
        if (notiType != "DEF") {
            destroyCurrentNotification()
            notiType = "DEF"
        } else {
            return
        }
        // B. Tạo pendingIntent cho notify
        val intentHome = Intent(this, ManHinhKhoiDong::class.java)
        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intentHome, 0)

        // c. Hiển thị noti và chạy services ngầm
        notification = NotificationCompat.Builder(this, mNotificationChannelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.da_dong_bo_tat_ca))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notiManager.notify(mNotificationId, notification)
    }

    private fun syncingNotification(tripSyncLeft: Int) {
        if (notiType != "SYNCING" || tripSyncLeft != previousNumber) {
            destroyCurrentNotification()
            notiType = "SYNCING"
            previousNumber = tripSyncLeft
        } else {
            return
        }
        // B. Tạo pendingIntent cho notify
//        val intentHome = Intent(this, ManHinhKhoiDong::class.java)
//        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        val pendingIntent = PendingIntent.getActivity(this, 0, intentHome, 0)

        // c. Hiển thị noti và chạy services ngầm
        notification = NotificationCompat.Builder(this, mNotificationChannelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.con_d_chuyen, tripSyncLeft))
            .setSmallIcon(R.mipmap.ic_launcher_round)
//            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setProgress(100, 30, true)
            .build()

        notiManager.notify(mNotificationId, notification)
    }

    fun destroyCurrentNotification() {
        try {
            notiManager.cancelAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    //========= XỬ LÝ TÁC VỤ TẠI ĐÂY ==========//
    fun sync() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                var tid = TripWaitForSync(this@SyncService).getAll()
                tid = tid.filter { !it.trip.trip.isSynced }.toTypedArray()
                if (tid.isNotEmpty()) {
                    syncingNotification(tid.size)
                    val currentTripInDb = TripWaitForSync(this@SyncService).getAll().last()
                    var currentTrip = currentTripInDb.trip
                    val nextTripId = SyncTripHistory().execute().get()
                    if (nextTripId > 0) {
                        val temp = SyncImages(currentTrip).execute().get()
                        if (temp != null) {
                            currentTrip = temp
                            currentTrip.trip.tripNumber = nextTripId
                            currentTrip.trip.isSynced = true
                            val resCode = SyncTrip(currentTrip).execute().get()
                            if (resCode == 200) {
                                TripWaitForSync(this@SyncService).remove(currentTripInDb.id)
                            } else if (resCode == 500) {
                                currentTripInDb.trip = currentTrip
                                TripWaitForSync(this@SyncService).update(currentTripInDb)
                            }
                            SyncTripHistory().execute().get()
                        }
                    }
                } else {
                    defaultNotification()
                }
            }
        }, 1000, 5000)
    }
}
