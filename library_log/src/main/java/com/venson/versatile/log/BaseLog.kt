package com.venson.versatile.log

import android.util.Log
import com.google.gson.GsonBuilder
import com.venson.versatile.log.print.BasePrint
import com.venson.versatile.log.print.DefaultPrint
import com.venson.versatile.log.print.JsonPrint
import com.venson.versatile.log.print.XmlPrint

internal object BaseLog {

    /*
    stackTraceIndex默认取值6
    0->VMStack.java#getThreadStackTrace
    1->Thread.java#getStackTrace
    2->BaseLog.kt#getTraceInfo
    3->BaseLog.kt#wrapperContent
    4->BaseLog.kt#printLog
    5->VLog.kt#d
     */
    private const val stackTraceIndex = 6

    private val gson by lazy {
        GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    /**
     * 日志输出
     * 接收参数必须全部传值或null，不可省略，否则影响stackTraceIndex层级
     */
    fun printLog(type: Int, msg: Any?, tagStr: String?) {
        val contents = wrapperContent(tagStr)
        val tag = contents[0]
        val headString = contents[1]
        /*
        content解析
         */
        var isObject = false
        val content: String? = if (tag == null && msg == null) {
            BasePrint.NULL_TIPS
        } else if (msg == null) {
            null
        } else if (msg is String) {
            msg
        } else if (msg is Throwable) {
            Log.getStackTraceString(msg)
        } else {
            try {
                gson.toJson(msg).let {
                    if (it.isNullOrEmpty() || it == "{}") {
                        isObject = false
                        msg.toString()
                    } else {
                        isObject = true
                        it
                    }
                }
            } catch (e: Exception) {
                isObject = false
                msg.toString()
            }
        }
        val headContent = headString ?: ""
        val msgContent = content ?: ""
        /*
        打印日志
         */
        if (VLog.printLogEnable() || VLog.saveLogEnable()) {
            when {
                type == VLog.JSON || isObject -> {
                    JsonPrint.print(type, tag, headContent, msgContent)
                }
                type == VLog.XML -> {
                    XmlPrint.print(type, tag, headContent, msgContent)
                }
                else -> {
                    DefaultPrint.print(type, tag, headContent, msgContent)
                }
            }
        }
    }

    /**
     * 拼接日志内容
     */
    private fun wrapperContent(tagStr: String?): Array<String?> {
        var targetTraceElement: Array<Any>? = null
        if (VLog.traceLogEnable()) {
            try {
                targetTraceElement = getTraceInfo()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        var className: String? = null
        var headString: String? = null
        targetTraceElement?.let {
            className = it[0].toString().let { fileName ->
                if (fileName.indexOf(".") > 0) {
                    fileName.substring(0, fileName.indexOf("."))
                } else {
                    fileName
                }
            }
            headString = it[1].toString()
        }
        var tag = tagStr ?: className
        if (VLog.globalTag().trim().isEmpty() && tag.isNullOrEmpty()) {
            tag = VLog.TAG_DEFAULT
        } else if (VLog.globalTag().trim().isNotEmpty() && tagStr.isNullOrEmpty()) {
            tag = VLog.globalTag().let {
                if (!className.isNullOrEmpty()) {
                    "$it$${className!!}"
                } else {
                    it
                }
            }
        }
        return arrayOf(tag, headString)
    }

    /**
     * 获取跳转信息
     */
    private fun getTraceInfo(): Array<Any> {
        val stackTrace = Thread.currentThread().stackTrace
        val stackTraceElement = stackTrace[stackTraceIndex]
        val filename = stackTraceElement.fileName
        val atInfo = stackTraceElement.toString()
        return arrayOf(filename, atInfo)
    }
}