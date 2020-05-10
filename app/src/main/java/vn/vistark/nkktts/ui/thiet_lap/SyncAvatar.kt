package vn.vistark.nkktts.ui.thiet_lap

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.api.RetrofitClient
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.utils.FileUtils
import java.io.File
import java.lang.Exception
import java.net.URI

class SyncAvatar(val context: AppCompatActivity, var uri: Uri, val onFinish: () -> Unit) :
    AsyncTask<Void, Void, Unit>() {
    companion object {
        fun syncFromServer(context: AppCompatActivity, path: String) {
            context.runOnUiThread {
                syncFromServerAsync(context, path).execute().get()
            }
        }
    }

    class syncFromServerAsync(val context: AppCompatActivity, val path: String) :
        AsyncTask<Void, Void, Unit>() {
        override fun doInBackground(vararg params: Void?) {
            val imgUrl =
                URI.create(RetrofitClient.getClient()!!.baseUrl().toString())
                    .resolve(path).toURL()
            val bm = BitmapFactory.decodeStream(
                imgUrl.openConnection().getInputStream()
            )
            FileUtils.saveAvatar(context, bm)
        }

    }

    override fun doInBackground(vararg params: Void?) {
        val bm = FileUtils.getCapturedImage(context, uri)
        val s = FileUtils.SaveSpiceImages(context, "avatar", bm)
        val f = File(s)
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
                        println("Đăng tải thành công ảnh $path")
                        // Sync, update profile luôn
                        val updateRes =
                            APIUtils.mAPIServices?.profileUpdateAPI(Constants.userInfo)?.execute()
                        if (updateRes != null && updateRes.isSuccessful) {
                            Constants.userInfo.image = path;
                            Constants.updateUserInfo()
                            println(
                                "Cập nhật UserProfile: ${GsonBuilder().create()
                                    .toJson(Constants.userInfo)}"
                            )
                            println("Cập nhật hồ sơ người dùng khi thay đổi avatar thành công!")
                            syncFromServer(context, path)
                        }
                    }
                }
            } catch (ez: Exception) {
                ez.printStackTrace()
            }
            f.delete()
        } else {
            println(">>>> TỆP TIN KHÔNG TỒN TẠI")
        }
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        onFinish()
    }
}