package com.venson.versatile.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.venson.versatile.app.databinding.ActivityMainBinding
import com.venson.versatile.log.*
import com.venson.versatile.log.interceptor.LogInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private lateinit var mainAdapter: MainAdapter

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(LogInterceptor())
            .build()
    }

    var waitRequestPermissionList: List<String> = arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.testButton.setOnClickListener {
            getHTML("https://developer.aliyun.com/mvn/guide")
            val domain = "https://apis.map.qq.com/ws/district/v1"
            val key = "OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77"
            val referer = "https://lbs.qq.com/"
            getJSON(
                "$domain/search?&keyword=${URLEncoder.encode("浙江省", "UTF-8")}&key=$key",
                referer
            )
            getJSON("$domain/getchildren?id=330000&key=$key", referer)
            getJSON("$domain/getchildren?id=340000&key=$key", referer)
            getJSON("$domain/getchildren?id=350000&key=$key", referer)
            getJSON("$domain/getchildren?id=210000&key=$key", referer)
            "测试sdgsd数据".logE()
            "测sgvs试数据".logW()
            "测sdvgsd试数据".logA()
            "{\"test\":\"value\"}".logJson()
            it.logD()
            it.also {
                lifecycleScope.launch {
                    logW()
                    try {
                        val d: String? = null
                        d!!.toString()
                    } catch (e: Exception) {
                        e.logW("nullTest")
                    }
                }
            }
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity, RecyclerView.VERTICAL, false
            )
            adapter = MainAdapter().also {
                mainAdapter = it
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getData(applicationContext)//下拉刷新
        }
        observeData()
        viewModel.getPackageNameList(applicationContext)
//        VLog.logDatabaseZipFile(progressListener = object : VLog.OnDatabaseFileZipProgressListener {
//            override fun onProgress(progress: Float) {
//                progress.logE()
//            }
//
//            override fun onSuccess(zipFile: File) {
//                zipFile.logE()
//            }
//
//            override fun onFailed(throwable: Throwable) {
//                throwable.printStackTraceByVLog()
//            }
//
//        })
//        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
//            val directory = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOWNLOADS
//            )
//            val path = directory.absolutePath + "/db_versatile_log"
//            path.logE()
//            val database = LogDatabase.getExternalInstance(this, packageName, path)
//            database.httpLogDao().getAllLog().logE()
//        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            && !Environment.isExternalStorageManager()
        ) {
            val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
        } else {
            applyPermission()
        }
    }

    private fun applyPermission(from: Int = 0) {
        val permission = try {
            waitRequestPermissionList[from]
        } catch (e: Exception) {
            actionDo()
            return
        }
        val isLastPermission = from >= waitRequestPermissionList.size - 1
        /*
        已授权
         */
        if (ActivityCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLastPermission) {
                actionDo()
                return
            }
            applyPermission(from + 1)
            return
        }
        /*
        未授权
         */
        val isShouldShowDialog = ActivityCompat
            .shouldShowRequestPermissionRationale(this, permission)
        if (isShouldShowDialog) {
            AlertDialog.Builder(this)
                .setMessage("要申请权限$permission")
                .setPositiveButton("ok") { dialog, which ->
                    dialog.dismiss()
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    "shouldShowRequestPermissionRationale call".logW("PermissionRequest")
                    applyPermission(from + 1)
                }
                .show()
            return
        }
        ActivityCompat.requestPermissions(this, arrayOf(permission), permission.hashCode())
        "第一次或者最后一次权限申请 $permission".logW("PermissionRequest")
        applyPermission(from + 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            "$permissions 已经授权".logD()
            permissions.forEach { permission ->
                setOnceDenied(permission)
            }
            return
        }
        permissions.forEachIndexed { index, permission ->
            val isShouldShowDialog = ActivityCompat
                .shouldShowRequestPermissionRationale(this, permission)
            if (isShouldShowDialog) {
//                AlertDialog.Builder(this)
//                    .setMessage("$permission 需要申请")
//                    .set
            }
        }
//        when (requestCode) {
//            REQUEST_CODE_CAMERA -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    findViewById<TextView>(R.id.tvStatus).text =
//                        getString(R.string.permission_granted)
//                } else {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(
//                            this,
//                            Manifest.permission.CAMERA
//                        )
//                    ) {
//                        setCameraDenied()
//                        showPermissionDeniedDialog(Manifest.permission.CAMERA, REQUEST_CODE_CAMERA)
//                    } else {
//                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
//                                this,
//                                Manifest.permission.CAMERA
//                            ) && (AppPreferences.cameraPermissionDeniedOnce)
//                        ) {
//                            showMandatoryPermissionsNeedDialog()
//                        }
//                    }
//                }
//            }
//        }
    }

    private fun setOnceDenied(permission: String) {
        getSharedPreferences(packageName, MODE_PRIVATE)?.let { sharedPreferences ->
            sharedPreferences.edit()?.let { editor ->
                editor.putBoolean(permission, true)
                editor.commit()
            }
        }
    }

    private fun getOnceDenied(permission: String): Boolean {
        getSharedPreferences(packageName, MODE_PRIVATE)?.let { sharedPreferences ->
            return sharedPreferences.getBoolean(permission, false)
        }
        return false
    }

    private fun showPermissionDeniedDialog(permission: String) {
        AlertDialog.Builder(this).apply {
            setCancelable(true)
            setMessage("申请权限$permission")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission),
                    permission.hashCode()
                )
            }
        }.show()
    }

    private fun showMandatoryPermissionsNeedDialog(permission: String) {
        AlertDialog.Builder(this).apply {
            setCancelable(true)
            setMessage("跳转设置页 允许权限$permission")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }.show()
    }

    private fun actionDo() {

    }

    private fun observeData() {
        viewModel.packageNameList.observe(this) {
            it.logD("packageNameList")
        }
        viewModel.data.observe(this) {
            binding.tipView.visibility = View.GONE
            if (it == null) {
                binding.swipeRefreshLayout.isRefreshing = true
                return@observe
            }
            binding.swipeRefreshLayout.isRefreshing = false
            mainAdapter.notifyData(it)
            if (it.isEmpty()) {
                binding.tipView.visibility = View.VISIBLE
            }
        }
    }

    private fun getHTML(url: String, referer: String? = null) {
        val request = Request.Builder()
            .url(url)
            .apply {
                referer?.let {
                    addHeader("Referer", it)
                }
            }
            .build()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = okHttpClient.newCall(request).execute()
                val data = response.body()?.string()
//                data.logXml("xml")
            } catch (e: Exception) {
                e.printStackTraceByVLog()
            }
        }
    }

    private fun getJSON(url: String, referer: String? = null) {
        val request = Request.Builder()
            .url(url)
            .post(
                MultipartBody.Builder()
                    .addFormDataPart("test", "123")
                    .addFormDataPart("33", "浙江省")
                    .addPart(MultipartBody.Part.createFormData("34", "福建省"))
                    .build()
            )
            .apply {
                referer?.let {
                    addHeader("Referer", it)
                }
            }
            .build()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = okHttpClient.newCall(request).execute()
                val data = response.body()?.string()
//                data.logJson("json")
            } catch (e: Exception) {
                e.printStackTraceByVLog()
            }
        }
    }

}