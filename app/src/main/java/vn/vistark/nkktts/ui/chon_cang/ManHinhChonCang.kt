package vn.vistark.nkktts.ui.chon_cang

import SeaPorts
import SeaPortsReponse
import TheTripStorage
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.man_hinh_chon_cang.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhChonCang : AppCompatActivity() {
    val seaPorts: ArrayList<SeaPorts> = ArrayList()
    lateinit var pDialog: SweetAlertDialog
    lateinit var cangAdapter: CangAdapter
    var loaiCang = cangDi

    companion object {
        const val chonLoaiCang = "CHON_LOAI_CANG"
        const val cangDi = "CANG_DI"
        const val cangVe = "CANG_VE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_chon_cang)

        val extras = intent.extras
        if (extras?.getString(chonLoaiCang) != null) {
            loaiCang = extras.getString(chonLoaiCang)!!
        }
        initPreComponents()

        ToolbarBackButton(this).show()

        supportActionBar?.title = getString(R.string.chon_cang)

        initEvents()
    }

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = getString(R.string.dang_xu_ly)
        pDialog.setCancelable(false)
    }

    private fun initDsCangBien() {
        mhccRvDanhSachCangBien.setHasFixedSize(true)
        mhccRvDanhSachCangBien.layoutManager = LinearLayoutManager(this)
        cangAdapter = CangAdapter(seaPorts)
        cangAdapter.onPortClick = {
            if (loaiCang == cangDi) {
                Constants.currentTrip = TheTripStorage()
                Constants.currentTrip.trip.departurePort = it.id
            } else {
                Constants.currentTrip.trip.destinationPort = it.id
            }
            if (Constants.updateCurrentTrip()) {
                setResult(Activity.RESULT_OK, Intent())
                finish()
            } else {
                SimpleNotify.error(this, getString(R.string.chon_cang_khong_duoc).toUpperCase(), "")
            }
        }
        mhccRvDanhSachCangBien.adapter = cangAdapter
    }

    fun processing() {
        if (!pDialog.isShowing) {
            pDialog.show()
        }
    }

    fun processed() {
        if (pDialog.isShowing) {
            pDialog.dismiss()
        }
    }

    private fun initEvents() {
        processing()
        val seaPortsReponse =
            OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts)
        if (seaPortsReponse?.seaPorts != null) {
            seaPorts.addAll(seaPortsReponse.seaPorts)
            initDsCangBien()
//            cangAdapter.notifyDataSetChanged()
            initFindEvents()
        } else {
            SimpleNotify.error(
                this@ManHinhChonCang,
                getString(R.string.lay_cang_khong_duoc).toUpperCase(),
                getString(R.string.vui_long_thu_lai)
            )
        }
        processed()
    }

    private fun initFindEvents() {
        val tempArr: Array<SeaPorts> = seaPorts.toTypedArray()
        edtSearchAU.addTextChangedListener(object : TextWatcher {
            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(s: Editable?) {
                val key = edtSearchAU.text.toString()
                if (key.isNotEmpty()) {
                    val ar2 = ArrayList(tempArr.toList()).filter {
                        it.name!!.toLowerCase().contains(key.toLowerCase()) ||
                                key.toLowerCase().contains(it.name.toLowerCase())
                    }
                    ar2.sortedBy { it.name }
                    println(">>>>" + ar2.size)
                    seaPorts.clear()
                    cangAdapter.notifyDataSetChanged()
                    seaPorts.addAll(ar2)
                    cangAdapter.notifyDataSetChanged()
                    println(seaPorts.size)
                } else {
                    seaPorts.clear()
                    seaPorts.addAll(tempArr)
                    seaPorts.sortBy { it.name }
                    cangAdapter.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SimpleNotify.onBackConfirm(this) {
            finish()
            super.onBackPressed()
        }
    }
}
