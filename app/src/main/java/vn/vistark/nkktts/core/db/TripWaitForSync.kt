package vn.vistark.nkktts.core.db

import TheTripStorage
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.Exception

class TripWaitForSync(context: Context) :
    SQLiteOpenHelper(context, TripWaitForSync::class.java.simpleName, null, 1) {

    class TripInDb(val id: Long, var trip: TheTripStorage)

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE ${TripWaitForSync::class.java.simpleName} (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "trip_json TEXT);"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${TripWaitForSync::class.java.simpleName}")
        onCreate(db)
    }

    fun add(tripStorage: TheTripStorage) {
        val s = GsonBuilder().create().toJson(tripStorage)
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("trip_json", s)
        db.insert(TripWaitForSync::class.java.simpleName, null, values)
        db.close()
    }

    fun getAll(): Array<TripInDb> {
        val db = this.readableDatabase
        var ttss = emptyArray<TripInDb>()
        val cursor =
            db.rawQuery("SELECT * FROM ${TripWaitForSync::class.java.simpleName}", emptyArray())
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val s = cursor.getString(1)
                try {
                    val tid = TripInDb(
                        cursor.getLong(0),
                        GsonBuilder().create().fromJson(s, TheTripStorage::class.java)
                    )
                    ttss = ttss.plus(tid)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        db.close()
        return ttss
    }

    fun getNotSync(): Array<TripInDb> {
        val db = this.readableDatabase
        var ttss = emptyArray<TripInDb>()
        val cursor =
            db.rawQuery("SELECT * FROM ${TripWaitForSync::class.java.simpleName}", emptyArray())
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val s = cursor.getString(1)
                try {
                    val tid = TripInDb(
                        cursor.getLong(0),
                        GsonBuilder().create().fromJson(s, TheTripStorage::class.java)
                    )
                    if (!tid.trip.trip.isSynced) {
                        ttss = ttss.plus(tid)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        db.close()
        return ttss
    }

    fun remove(id: Long): Int {
        val db = this.writableDatabase
        val res = db.delete(
            TripWaitForSync::class.java.simpleName,
            "id=?",
            arrayOf(id.toString())
        )
        db.close()
        return res
    }

    fun update(trip: TripInDb) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("trip_json", GsonBuilder().create().toJson(trip.trip))
        db.update(TripWaitForSync::class.java.simpleName, values, "id=${trip.id}", null)
    }
}