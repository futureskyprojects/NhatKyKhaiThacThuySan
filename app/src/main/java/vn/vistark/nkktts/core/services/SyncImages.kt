package vn.vistark.nkktts.core.services

import TheTripStorage
import android.os.AsyncTask
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.vistark.nkktts.core.api.APIUtils
import java.io.File
import java.lang.Exception

class SyncImages(var tripStorage: TheTripStorage) : AsyncTask<Void, Void, TheTripStorage?>() {
    override fun doInBackground(vararg params: Void?): TheTripStorage? {
        var isSuccess = true
        // Tiến hành đồng bộ ảnh
        for (i in tripStorage.trip.hauls.indices) {
            for (j in tripStorage.trip.hauls[i].spices.indices) {
                val imgArr = GsonBuilder().create()
                    .fromJson(
                        tripStorage.trip.hauls[i].spices[j].images,
                        Array<String>::class.java
                    )
                for (k in imgArr) {
                    val f = File(k)
                    if (f.exists()) {
                        val imageFileBody = RequestBody.create(
                            MediaType.parse("image/jpeg"),
                            f
                        )

                        try {
                            // Gửi yêu cầu và lấy phản hồi
                            val res = APIUtils.mAPIServices?.uploadImage(
                                MultipartBody.Part.createFormData(
                                    "image",
                                    f.name,
                                    imageFileBody
                                )
                            )?.execute()

                            // Xử lý phản hồi
                            if (res != null && res.isSuccessful) {
                                val path = res.body()?.result?.path
                                if (path != null) {
                                    do {
                                        try {
                                            tripStorage.trip.hauls[i].spices[j].images =
                                                tripStorage.trip.hauls[i].spices[j].images.replace(
                                                    k,
                                                    path
                                                )
                                            println("Uploaded $k to $path")
                                            break
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } while (true)
                                }
                            } else {
                            }
                        } catch (ez: Exception) {
                            ez.printStackTrace()
                            isSuccess = false
                        }
                    }
                }
            }
        }
        if (isSuccess) {
            return tripStorage
        } else {
            return null
        }
    }
}