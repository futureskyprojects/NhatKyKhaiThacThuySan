package vn.vistark.nkktts.ui.thiet_lap

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.man_hinh_thiet_lap.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.RetrofitClient
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.doi_mat_khau.ManHinhDoiMatKhau
import vn.vistark.nkktts.ui.khoi_dong.ManHinhKhoiDong
import vn.vistark.nkktts.ui.sua_ho_so.ManHinhSuaHoSo
import vn.vistark.nkktts.utils.FileUtils
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton
import java.io.File
import java.net.URI


class ManHinhThietLap : AppCompatActivity() {
    private val REQUEST_PICK_PHOTO: Int = 33222
    private val REQUEST_TAKE_PHOTO: Int = 33221
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_thiet_lap)

        ToolbarBackButton(this).show()

        initData()

        initEvents()

        supportActionBar?.title = getString(R.string.thiet_lap)

        showAvatar()
    }

    private fun initData() {
        mhtlTvTenThuyenTruong.text = Constants.userInfo.captain
    }

    private fun initEvents() {
        rlBtnSuaThongTin.setOnClickListener {
            val intent = Intent(this, ManHinhSuaHoSo::class.java)
            startActivity(intent)
        }

        rlBtnDoiNghe.setOnClickListener {
            val intent = Intent(this, ManHinhDanhSachNghe::class.java)
            ManHinhDanhSachNghe.isEdit = true
            startActivity(intent)
        }

        rlBtnDoiMatKhau.setOnClickListener {
            val intent = Intent(this, ManHinhDoiMatKhau::class.java)
            ManHinhDoiMatKhau.isChangePassword = true
            startActivity(intent)
        }

        mhtlBtnDangXuat.setOnClickListener {
            SweetAlertDialog(this).apply {
                titleText = getString(R.string.dang_xuat).toUpperCase()
                contentText = getString(R.string.ban_co_chac_chan)
                setConfirmButton(getString(R.string.dong_y)) {
                    it.dismiss()
                    if (Constants.logOut()) {
                        FileUtils.removeAvatar(this@ManHinhThietLap)
                        val intent = Intent(this@ManHinhThietLap, ManHinhKhoiDong::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        SimpleNotify.error(
                            this@ManHinhThietLap,
                            getString(R.string.dang_xuat_loi).toUpperCase(),
                            getString(R.string.vui_long_thu_lai)
                        )
                    }
                }
                setCancelButton(getString(R.string.quay_ve)) {
                    it.dismissWithAnimation()
                }
                show()
            }
        }

        ivMhtlEnUS.setOnClickListener {
            changeLanguage("en")
        }
        ivMhtlViVN.setOnClickListener {
            changeLanguage("vi")
        }

        mhtlNutDoiAnhDaiDien.setOnClickListener {
            selectImage()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun changeLanguage(lanCode: String) {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).apply {
            titleText =
                getString(R.string.moi_thay_doi_se_duoc_cap_nhat_khi_khoi_dong_lai_ung_dung).toUpperCase()
            setConfirmButton(getString(R.string.dong_y)) {
                it.dismissWithAnimation()
                OfflineDataStorage.saveData("lang_code", lanCode)
            }
            setCancelButton(getString(R.string.huy)) {
                it.dismissWithAnimation()
            }
            show()
        }
    }

    private fun selectImage() {
        val options =
            arrayOf<CharSequence>(
                getString(R.string.chup_anh), getString(R.string.chon_anh), getString(
                    R.string.dong
                )
            )
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.chon_phuong_thuc_lay_anh))
        builder.setItems(options) { dialog, index ->
            when (index) {
                0 -> {
                    val values = ContentValues()
                    values.put(
                        MediaStore.Images.Media.TITLE,
                        "Vistark_${System.currentTimeMillis()}"
                    )
                    values.put(
                        MediaStore.Images.Media.DESCRIPTION,
                        "Write new app? contact projects.futuresky@gmail.com"
                    )

                    imageUri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    );
                    val takePicture =
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(takePicture, REQUEST_TAKE_PHOTO)
                }
                1 -> {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhoto, REQUEST_PICK_PHOTO)
                }
                2 -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                    if (imageUri != null) {
                        mhtlIsLoadingAvatar.visibility = View.VISIBLE
                        runOnUiThread {
                            SyncAvatar(this, imageUri!!) {
                                showAvatar()
                            }.execute()
                        }
                    }
                }
                REQUEST_PICK_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                    val selectedImage: Uri? = data?.data
                    if (selectedImage != null) {
                        mhtlIsLoadingAvatar.visibility = View.VISIBLE
//                        println(selectedImage.toFile().toString() + " >>>> ĐƯỢC CHỌN")
                        runOnUiThread {
                            SyncAvatar(this, selectedImage) {
                                showAvatar()
                            }.execute()
                        }
                    }
                }
            }
        }
    }


    private fun showAvatar() {
        Thread {
            val bm = FileUtils.getAvatarBitmap(this)
            if (bm != null) {
                mhtlAnhNguDan.post {
                    mhtlAnhNguDan.setImageBitmap(bm)
                }
            } else {
                mhtlAnhNguDan.post {
                    mhtlAnhNguDan.setImageResource(R.drawable.ic_fisherman)
                }
            }
            mhtlIsLoadingAvatar.post {
                mhtlIsLoadingAvatar.visibility = View.GONE
            }
        }.start()
    }
}
