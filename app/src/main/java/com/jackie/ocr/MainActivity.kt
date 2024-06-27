package com.jackie.ocr

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import cc.shinichi.library.ImagePreview
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jackie.ocr.adapter.MainAdapter
import com.jackie.ocr.bean.IDBean
import com.jackie.ocr.databinding.ActivityMainBinding
import com.lcl.ocr.OnOCRResultListener
import com.lcl.ocr.india.IndiaOcrRecognizer

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mAdapter: MainAdapter = MainAdapter()

    private val TAG = "文字识别"

    // 图片识别数据
    private var imgList = mutableListOf<IDBean>()

    // 收集图片的大小
    private var imgSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mAdapter
        }
        // 需要传递控件 id
        mAdapter.addOnItemChildClickListener(R.id.ivImage) { adapter, view, position ->
            ImagePreview.instance
                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好；
                .setContext(this)
                // 设置从第几张开始看（索引从0开始）
                .setIndex(0)
                // 只有一张图片的情况，可以直接传入这张图片的url
                .setImage(mAdapter.getItem(position)?.imgPath.toString())
                // 开启预览
                .start()
        }
        setPermissions()
    }

    private fun setPermissions() {
        XXPermissions.with(this)
            // 申请单个权限
            .permission(Permission.READ_MEDIA_IMAGES)
            // 申请多个权限
//            .permission(Permission.Group.CALENDAR)
            .request(object : OnPermissionCallback {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        Toast.makeText(
                            this@MainActivity,
                            "获取部分权限成功，但部分权限未正常授予",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    loadAndDetectImage()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
//                        toast("被永久拒绝授权，请手动授予录音和日历权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                    } else {
//                        toast("获取录音和日历权限失败")
                    }
                }
            })
    }

    /**
     * 加载并检测图片
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadAndDetectImage() {
        // 图片列表
        val photoList = getAllPhoto()
        imgSize = photoList.size
        for (path in photoList) {
            // 图片地址转Bitmap
            val imageBitmap = BitmapFactory.decodeFile(path)
            IndiaOcrRecognizer.getInstance().getAadhaarInfo(imageBitmap, object :
                OnOCRResultListener {
                override fun onSuccess(ocrInfo: HashMap<String, String>?) {
                    setAadhaarCardInfo(ocrInfo, path, true)
                }

                override fun onFailure(e: Exception) {

                }
            })
        }
    }

    /**
     * 设置Aadhaar卡片信息数据
     */
    private fun setAadhaarCardInfo(
        hashMap: HashMap<String, String>?,
        path: String,
        isSuccess: Boolean
    ) {
        // 填充数据
        val idBean = IDBean()
        idBean.imgPath = path
        if (isSuccess) {
            // 识别出来的文字已经做了分类，把对应的数据设置即可
            if (hashMap != null) {
                for (bean in hashMap) {
                    when (bean.key) {
                        // 性别
                        "gender" -> {
                            idBean.gender = bean.value
                        }
                        // 年份
                        "date" -> {
                            idBean.date = bean.value
                        }
                        // 号码
                        "id" -> {
                            idBean.id = bean.value
                        }
                        // 卡片类型
                        "id_card_type" -> {
                            idBean.id_card_type = bean.value
                        }
                        // 姓名
                        "name" -> {
                            idBean.name = bean.value
                        }
                    }
                }
            }
        }
        // 添加进集合
        imgList.add(idBean)
        if (imgList.size == imgSize) {
            mAdapter.submitList(imgList)
        }
    }

    /**
     * 获取手机里面所有图片
     */
    @SuppressLint("Range")
    private fun getAllPhoto(): List<String> {
        // 图片数据集合
        val imagesList: MutableList<String> = ArrayList()
        val imageCursor: Cursor? = myContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.DISPLAY_NAME
            ),
            MediaStore.Images.Media.MIME_TYPE + "=? or " +
                    MediaStore.Images.Media.MIME_TYPE + "=? or " +
                    MediaStore.Images.Media.MIME_TYPE + "=?",
            arrayOf("image/jpeg", "image/png", "image/jpg"),
            MediaStore.Images.Media.DATE_MODIFIED + " desc"
        )
        if (imageCursor != null) {
            while (imageCursor.moveToNext()) {
                try {
                    // 图片地址
                    val path =
                        imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    imagesList.add(path)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            imageCursor.close()
        }
        return imagesList
    }
}