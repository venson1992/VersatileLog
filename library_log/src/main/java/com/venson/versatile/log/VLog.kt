package com.venson.versatile.log

/**
 * 日志工具类
 */
object VLog {

    const val TAG_DEFAULT = "VLog"

    //providerAction
    const val ACTION_PROVIDER = "com.venson.versatile.log.PROVIDER"

    //是否打印日志
    private var isPrintLog: Boolean = BuildConfig.DEBUG

    //是否开启任务栈
    private var isTraceEnable: Boolean = true

    //全局tag
    private var globalTag: String = ""

    //是否保存日志
    private var isSaveLog: Boolean = true

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

    @JvmStatic
    fun v() {
        BaseLog.printLog(BaseLog.V, null, null)
    }

    @JvmStatic
    fun v(msg: Any?) {
        BaseLog.printLog(BaseLog.V, msg, null)
    }

    @JvmStatic
    fun v(tag: String?, msg: Any?) {
        BaseLog.printLog(BaseLog.V, msg, tag)
    }

    @JvmStatic
    fun d() {
        BaseLog.printLog(BaseLog.D, null, null)
    }

    @JvmStatic
    fun d(msg: Any?) {
        BaseLog.printLog(BaseLog.D, msg, null)
    }

    @JvmStatic
    fun d(tag: String?, msg: Any?) {
        BaseLog.printLog(BaseLog.D, msg, tag)
    }

    @JvmStatic
    fun i() {
        BaseLog.printLog(BaseLog.I, null, null)
    }

    @JvmStatic
    fun i(msg: Any?) {
        BaseLog.printLog(BaseLog.I, msg, null)
    }

    @JvmStatic
    fun i(tag: String?, msg: Any?) {
        BaseLog.printLog(BaseLog.I, msg, tag)
    }

    @JvmStatic
    fun w() {
        BaseLog.printLog(BaseLog.W, null, null)
    }

    @JvmStatic
    fun w(msg: Any?) {
        BaseLog.printLog(BaseLog.W, msg, null)
    }

    @JvmStatic
    fun w(tag: String?, msg: Any?) {
        BaseLog.printLog(BaseLog.W, msg, tag)
    }

    @JvmStatic
    fun e() {
        BaseLog.printLog(BaseLog.E, null, null)
    }

    @JvmStatic
    fun e(msg: Any?) {
        BaseLog.printLog(BaseLog.E, msg, null)
    }

    @JvmStatic
    fun e(tag: String?, msg: Any?) {
        BaseLog.printLog(BaseLog.E, msg, tag)
    }

    @JvmStatic
    fun a() {
        BaseLog.printLog(BaseLog.A, null, null)
    }

    @JvmStatic
    fun a(msg: Any?) {
        BaseLog.printLog(BaseLog.A, msg, null)
    }

    @JvmStatic
    fun a(tag: String?, msg: Any?) {
        BaseLog.printLog(BaseLog.A, msg, tag)
    }

    @JvmStatic
    fun json(jsonFormat: String?) {
        BaseLog.printLog(BaseLog.JSON, jsonFormat, null)
    }

    @JvmStatic
    fun json(tag: String?, jsonFormat: String?) {
        BaseLog.printLog(BaseLog.JSON, jsonFormat, tag)
    }

    @JvmStatic
    fun xml(xml: String?) {
        BaseLog.printLog(BaseLog.XML, xml, null)
    }

    @JvmStatic
    fun xml(tag: String?, xml: String?) {
        BaseLog.printLog(BaseLog.XML, xml, tag)
    }
}