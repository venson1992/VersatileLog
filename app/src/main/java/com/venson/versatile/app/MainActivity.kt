package com.venson.versatile.app

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.venson.versatile.app.databinding.ActivityMainBinding
import com.venson.versatile.log.*
import com.venson.versatile.log.interceptor.LogInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private var mBottomSheetBehavior: BottomSheetBehavior<out View>? = null

    private var mBottomSheetRect: Rect = Rect(0, 0, 0, 0)

    private lateinit var mainAdapter: MainAdapter

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(LogInterceptor())
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        BottomSheetBehavior.from(binding.bottomSheetView).also {
            mBottomSheetBehavior = it
            it.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset == 1.0F) {
                        val location = intArrayOf(0, 0)
                        bottomSheet.getLocationOnScreen(location)
                        mBottomSheetRect.left = location[0]
                        mBottomSheetRect.top = location[1]
                        mBottomSheetRect.right = mBottomSheetRect.left + bottomSheet.measuredWidth
                        mBottomSheetRect.bottom = mBottomSheetRect.top + bottomSheet.measuredHeight
                    }
                }

            })
        }
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
        binding.peekView.setOnClickListener {
            toggleBottom()
        }
//        binding.bottomSheetLayout.tagEditText.addTextChangedListener {
//            val text = it?.toString()
//            if (viewModel.tag.value == text) {
//                return@addTextChangedListener
//            }
//            viewModel.tag.value = text
//        }
//        binding.bottomSheetLayout.ignoreCaseCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isIgnoreCase.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isIgnoreCase.value = isChecked
//        }
//        binding.bottomSheetLayout.dayEditText.addTextChangedListener {
//            val day = it?.toString()?.toIntOrNull() ?: VLog.logStorageLifeInDay()
//            if (viewModel.day.value == day) {
//                viewModel.day.value = day
//            }
//        }
//        binding.bottomSheetLayout.vCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isVerboseChecked.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isVerboseChecked.value = isChecked
//        }
//        binding.bottomSheetLayout.dCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isDebugChecked.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isDebugChecked.value = isChecked
//        }
//        binding.bottomSheetLayout.iCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isInfoChecked.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isInfoChecked.value = isChecked
//        }
//        binding.bottomSheetLayout.wCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isWarnChecked.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isWarnChecked.value = isChecked
//        }
//        binding.bottomSheetLayout.eCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isErrorChecked.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isErrorChecked.value = isChecked
//        }
//        binding.bottomSheetLayout.aCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isAssertChecked.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isAssertChecked.value = isChecked
//        }
//        binding.bottomSheetLayout.jsonCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isJSONChecked.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isJSONChecked.value = isChecked
//        }
//        binding.bottomSheetLayout.xmlCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isXMLChecked.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isXMLChecked.value = isChecked
//        }
//        binding.bottomSheetLayout.otherCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            if (viewModel.isOtherChecked.value == isChecked) {
//                return@setOnCheckedChangeListener
//            }
//            viewModel.isOtherChecked.value = isChecked
//        }
        observeData()
        viewModel.getData(applicationContext)
    }

    private fun observeData() {
        viewModel.tag.observe(this) {
            binding.bottomSheetLayout.tagEditText.setText(it)
            viewModel.getData(applicationContext)
        }
        viewModel.isIgnoreCase.observe(this) {
            binding.bottomSheetLayout.ignoreCaseCheckBox.isChecked = it
            viewModel.getData(applicationContext)
        }
        viewModel.day.observe(this) {
            binding.bottomSheetLayout.dayEditText.setText(it.toString())
            viewModel.getData(applicationContext)
        }
        viewModel.isVerboseChecked.observe(this) {
            binding.bottomSheetLayout.vCheckBox.isChecked = it
            viewModel.getData(applicationContext)
        }
        viewModel.isDebugChecked.observe(this) {
            binding.bottomSheetLayout.dCheckBox.isChecked = it
            viewModel.getData(applicationContext)
        }
        viewModel.isInfoChecked.observe(this) {
            binding.bottomSheetLayout.iCheckBox.isChecked = it
            viewModel.getData(applicationContext)
        }
        viewModel.isWarnChecked.observe(this) {
            binding.bottomSheetLayout.wCheckBox.isChecked = it
            viewModel.getData(applicationContext)
        }
        viewModel.isErrorChecked.observe(this) {
            binding.bottomSheetLayout.eCheckBox.isChecked = it
            viewModel.getData(applicationContext)
        }
        viewModel.isAssertChecked.observe(this) {
            binding.bottomSheetLayout.aCheckBox.isChecked = it
            viewModel.getData(applicationContext)
        }
        viewModel.isJSONChecked.observe(this) {
            binding.bottomSheetLayout.jsonCheckBox.isChecked = it
            viewModel.getData(applicationContext)
        }
        viewModel.isXMLChecked.observe(this) {
            binding.bottomSheetLayout.xmlCheckBox.isChecked = it
            viewModel.getData(applicationContext)
        }
        viewModel.isOtherChecked.observe(this) {
            binding.bottomSheetLayout.otherCheckBox.isChecked = it
            viewModel.getData(applicationContext)
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val bottomSheetBehavior = mBottomSheetBehavior ?: let {
            return super.dispatchTouchEvent(ev)
        }
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED && ev != null) {
            if (ev.rawX >= mBottomSheetRect.left
                && ev.rawX <= mBottomSheetRect.right
                && ev.rawY >= mBottomSheetRect.top
                && ev.rawY <= mBottomSheetRect.bottom
            ) {
                return super.dispatchTouchEvent(ev)
            }
            binding.peekView.onTouchEvent(ev)
            return true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if (mBottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            toggleBottom()
            return
        }
        super.onBackPressed()
    }

    private fun toggleBottom() {
        mBottomSheetBehavior?.let {
            if (it.state == BottomSheetBehavior.STATE_EXPANDED) {
                it.state = BottomSheetBehavior.STATE_COLLAPSED
            } else if (it.state == BottomSheetBehavior.STATE_COLLAPSED) {
                it.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}