package vn.vistark.nkktts.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class FileUtils {
    companion object {
        fun getCapturedImage(context: Context, selectedPhotoUri: Uri): Bitmap {
            return if (Build.VERSION.SDK_INT >= 29) {
                // To handle deprication use
                val source =
                    ImageDecoder.createSource(context.contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                // Use older version
                MediaStore.Images.Media.getBitmap(context.contentResolver, selectedPhotoUri)
            }
        }

        fun SaveSpiceImages(context: AppCompatActivity, imgName: String, bm: Bitmap): String {
            val root = context.externalCacheDir!!.path + "/spices_image"
            val fz = File(root)
            if (!fz.exists()) {
                fz.mkdirs()
            }
            val fname = "${imgName}_${System.currentTimeMillis()}.jpg"
            val f = File(root, fname)
            if (f.exists()) {
                f.delete()
            }
            try {
                val out = FileOutputStream(f)
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                return f.path
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }

        fun removeAvatar(context: AppCompatActivity) {
            try {
                val avatarPath = context.externalCacheDir!!.path + "/avatar/avartar.jpg"
                val f = File(avatarPath)
                if (f.exists()) {
                    f.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun isAvatarExists(context: AppCompatActivity): Boolean {
            val avatarPath = context.externalCacheDir!!.path + "/avatar/avartar.jpg"
            return File(avatarPath).exists()
        }

        fun getAvatarBitmap(context: AppCompatActivity): Bitmap? {
            try {
                val avatarPath = context.externalCacheDir!!.path + "/avatar/avartar.jpg"
                return BitmapFactory.decodeFile(avatarPath)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun saveAvatar(context: AppCompatActivity, bm: Bitmap): String {
            val root = context.externalCacheDir!!.path + "/avatar"
            val fz = File(root)
            if (!fz.exists()) {
                fz.mkdirs()
            }
            val fname = "avartar.jpg"
            val f = File(root, fname)
            if (f.exists()) {
                f.delete()
            }
            try {
                val out = FileOutputStream(f)
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                return f.path
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }

        fun getBitmapFile(filePath: String): Bitmap? {
            val f = File(filePath)
            if (f.exists()) {
                return BitmapFactory.decodeFile(f.absolutePath)
            }
            return null
        }
    }
}