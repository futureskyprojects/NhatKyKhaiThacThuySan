package vn.vistark.nkktts.core.services

import android.os.AsyncTask
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.core.models.trip_history.HistoryTripSuccessResponse
import java.util.*

class SyncTripHistory : AsyncTask<Void, Void, Int>() {
    override fun doInBackground(vararg params: Void?): Int {
        try {
            val htsr = APIUtils.mAPIServices?.getHistoryTrip()?.execute()
            if (htsr != null && htsr.isSuccessful) {
                val htripDatas = htsr.body()?.data
                if (htripDatas != null) {
                    // Lưu dữ liệu lại, cũng là cập nhật mới
                    OfflineDataStorage.saveData(
                        OfflineDataStorage.tripHistory,
                        htripDatas
                    )
                    // Coppy để lấy các chuyến cùng năm
                    var tempHTripDatas =
                        emptyArray<HistoryTripSuccessResponse.HtripData>()
                    htripDatas.forEach {
                        val time =
                            it.submit_time ?: it.destination_time ?: it.created_at
                        val y = time.subSequence(0, 4).toString().toIntOrNull()
                        if (y != null && y >= Calendar.getInstance()
                                .get(Calendar.YEAR)
                        ) {
                            tempHTripDatas = tempHTripDatas.plus(it)
                        }
                    }
                    // Sắp xếp lại mảng để có danh sách lịch sử chuyến với trip_number giảm dần
                    tempHTripDatas =
                        tempHTripDatas.sortedByDescending { it.trip_number.toInt() }
                            .toTypedArray()
                    // Nếu dữ liệu trống thì khởi đầu là 1,
                    // Còn không thì lấy trip_number lớn nhất + 1
                    // Tiến hành đồng bộ ảnh trước
                    if (tempHTripDatas.isEmpty()) {
                        return 1
                    } else {
                        return tempHTripDatas.first().trip_number.toInt() + 1
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }
}