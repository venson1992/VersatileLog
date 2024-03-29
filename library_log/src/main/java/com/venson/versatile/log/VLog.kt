package com.venson.versatile.log

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.venson.versatile.log.database.LogDatabase
import com.venson.versatile.log.work.DefaultExecutorSupplier
import com.venson.versatile.log.work.ZipDatabaseTask
import java.io.File

/**
 * 日志工具类
 */
object VLog {

    const val TAG_DEFAULT = "VLog"

    /*
    日志类型
     */
    const val V = 0x01
    const val D = 0x02
    const val I = 0x03
    const val W = 0x04
    const val E = 0x05
    const val A = 0x06
    const val JSON = 0x07
    const val XML = 0x08
    const val HTTP = 0x09

    //providerAction
    const val ACTION_PROVIDER = "com.venson.versatile.log.PROVIDER"

    private var mExceptionCallback: ((e: Exception) -> Unit)? = null

    //是否打印日志
    private var isPrintLog: Boolean = BuildConfig.DEBUG

    //是否开启任务栈
    private var isTraceEnable: Boolean = true

    //全局tag
    private var globalTag: String = ""

    //是否保存日志
    private var isSaveLog: Boolean = true

    //应用实例
    private var applicationContext: Context? = null

    //数据库加密关键字
    private var encryptKey: String? = null

    /*
    是否开启自动初始化
    自动初始化则provide自动注册application
     */
    private var isAutoMaticInitial: Boolean = true

    //日志保存期限，默认7天
    private var storageLifeInDay: Int = 7

    /**
     * 设置是否开启打印日志
     */
    @JvmStatic
    fun printLogEnable(enable: Boolean): VLog {
        isPrintLog = enable
        return this
    }

    /**
     * 是否开启日志打印
     */
    @JvmStatic
    fun printLogEnable(): Boolean {
        return isPrintLog
    }

    /**
     * 是否开启堆栈信息
     */
    @JvmStatic
    fun traceLogEnable(enable: Boolean): VLog {
        isTraceEnable = enable
        return this
    }

    /**
     * 是否开启堆栈信息
     */
    @JvmStatic
    fun traceLogEnable(): Boolean {
        return isTraceEnable
    }

    /**
     * 设置全局标签
     */
    @JvmStatic
    fun globalTag(tag: String): VLog {
        globalTag = tag
        return this
    }

    /**
     * 获取全局tag
     */
    @JvmStatic
    fun globalTag(): String {
        return globalTag
    }

    /**
     * 设置是否保存日志
     */
    @JvmStatic
    fun saveLogEnable(enable: Boolean): VLog {
        isSaveLog = enable
        return this
    }

    /**
     * 是否开启保存日志
     */
    @JvmStatic
    fun saveLogEnable(): Boolean {
        return isSaveLog
    }

    /**
     * 默认开启自动初始化
     * 若要关闭自动逻辑，请在宿主APP的application中attachContext方法中调用该方法关闭
     */
    @JvmStatic
    fun automaticEnable(enable: Boolean): VLog {
        isAutoMaticInitial = enable
        return this
    }

    /**
     * 是否开启自动注册
     */
    @JvmStatic
    fun automaticEnable(): Boolean {
        return isAutoMaticInitial
    }

    /**
     * 初始化application
     * 手动初始化，需要现在application关闭自动初始化
     */
    @JvmStatic
    fun init(context: Context): VLog {
        applicationContext = context.applicationContext
        return this
    }

    /**
     * 获取applicationContext
     */
    @JvmStatic
    fun applicationContext(): Context? {
        return applicationContext
    }

    /**
     * 设置数据库加密密钥
     */
    @JvmStatic
    fun encryptedKey(key: String): VLog {
        encryptKey = key
        return this
    }

    /**
     * 数据库加密密钥
     */
    @JvmStatic
    fun encryptedKey(): String? {
        return encryptKey
    }

    /**
     * 设置日志本地化存储有效期
     * @param day 有效范围1-365天
     */
    @JvmStatic
    fun logStorageLifeInDay(@IntRange(from = 1L, to = 365L) day: Int): VLog {
        storageLifeInDay = day
        return this
    }

    /**
     * 获取本地化存储时效
     */
    @JvmStatic
    fun logStorageLifeInDay(): Int {
        return storageLifeInDay
    }

    /**
     * 获取数据库路径
     * @param packageName 查看指定应用的数据库路径，默认当前应用的包名
     */
    @JvmStatic
    fun logDatabasePath(packageName: String? = null): String {
        val context = applicationContext() ?: let {
            throw Exception("VLog 未初始化")
        }
        return LogDatabase.getDatabasePath(context, packageName)
    }

    /**
     * @param zipFilePath 导出的压缩包文件名
     * @param packageName 指定应用包名
     * @param progressListener 导出监听
     */
    @JvmStatic
    fun logDatabaseZipFile(
        zipFilePath: String? = null,
        packageName: String? = null,
        progressListener: OnDatabaseFileZipProgressListener
    ) {
        val context = applicationContext() ?: let {
            progressListener.onFailure(Throwable("VLog 未初始化"))
            return
        }
        val path = logDatabasePath(packageName)
        val pathList = mutableListOf<String>()
        pathList.add(path)
        pathList.add("$path-shm")
        pathList.add("$path-wal")
        DefaultExecutorSupplier.instance.forBackgroundTasks().execute(
            ZipDatabaseTask(
                packageName ?: context.packageName,
                zipFilePath,
                progressListener
            )
        )
    }

    /**
     * 获取本地已安装的接入该日志的应用列表
     */
    @JvmStatic
    fun getSupportedPackageNameList(): List<String> {
        val context = applicationContext() ?: let {
            throw Exception("VLog 未初始化")
        }
        val packageManager = context.packageManager
        val filter = Intent("com.venson.versatile.log.PROVIDER")
        val resolveInfoList = packageManager.queryIntentActivities(
            filter, PackageManager.GET_RESOLVED_FILTER
        )
        if (resolveInfoList.isEmpty()) {
            return emptyList()
        }
        val list = mutableListOf<String>()
        resolveInfoList.forEach { resolveInfo ->
            resolveInfo?.activityInfo?.packageName?.let { packagename ->
                list.add(packagename)
            }
        }
        return list
    }

    @JvmStatic
    fun v() {
        BaseLog.printLog(V, null, null)
    }

    @JvmStatic
    fun v(msg: Any?) {
        BaseLog.printLog(V, msg, null)
    }

    @JvmStatic
    fun v(tag: String?, msg: Any?) {
        BaseLog.printLog(V, msg, tag)
    }

    @JvmStatic
    fun d() {
        BaseLog.printLog(D, null, null)
    }

    @JvmStatic
    fun d(msg: Any?) {
        BaseLog.printLog(D, msg, null)
    }

    @JvmStatic
    fun d(tag: String?, msg: Any?) {
        BaseLog.printLog(D, msg, tag)
    }

    @JvmStatic
    fun i() {
        BaseLog.printLog(I, null, null)
    }

    @JvmStatic
    fun i(msg: Any?) {
        BaseLog.printLog(I, msg, null)
    }

    @JvmStatic
    fun i(tag: String?, msg: Any?) {
        BaseLog.printLog(I, msg, tag)
    }

    @JvmStatic
    fun w() {
        BaseLog.printLog(W, null, null)
    }

    @JvmStatic
    fun w(msg: Any?) {
        BaseLog.printLog(W, msg, null)
    }

    @JvmStatic
    fun w(tag: String?, msg: Any?) {
        BaseLog.printLog(W, msg, tag)
    }

    @JvmStatic
    fun e() {
        BaseLog.printLog(E, null, null)
    }

    @JvmStatic
    fun e(msg: Any?) {
        BaseLog.printLog(E, msg, null)
    }

    @JvmStatic
    fun e(tag: String?, msg: Any?) {
        BaseLog.printLog(E, msg, tag)
    }

    @JvmStatic
    fun a() {
        BaseLog.printLog(A, null, null)
    }

    @JvmStatic
    fun a(msg: Any?) {
        BaseLog.printLog(A, msg, null)
    }

    @JvmStatic
    fun a(tag: String?, msg: Any?) {
        BaseLog.printLog(A, msg, tag)
    }

    @JvmStatic
    fun json(jsonFormat: String?) {
        BaseLog.printLog(JSON, jsonFormat, null)
    }

    @JvmStatic
    fun json(tag: String?, jsonFormat: String?) {
        BaseLog.printLog(JSON, jsonFormat, tag)
    }

    @JvmStatic
    fun xml(xml: String?) {
        BaseLog.printLog(XML, xml, null)
    }

    @JvmStatic
    fun xml(tag: String?, xml: String?) {
        BaseLog.printLog(XML, xml, tag)
    }

    interface OnDatabaseFileZipProgressListener {
        fun onProgress(@FloatRange(from = 0.0, to = 1.0) progress: Float)

        fun onSuccess(zipFile: File)

        fun onFailure(throwable: Throwable)
    }

    fun setExceptionCallback(callback: (e: Exception) -> Unit) {
        mExceptionCallback = callback
    }

    fun callException(e1: Exception) {
        try {
            mExceptionCallback?.invoke(e1)
        } catch (e2: Exception) {
            e1.printStackTrace()
            e2.printStackTrace()
        }
    }
}