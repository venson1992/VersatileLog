package com.venson.versatile.log

import com.google.gson.Gson
import com.venson.versatile.log.print.BasePrint
import com.venson.versatile.log.print.DefaultPrint
import com.venson.versatile.log.print.JsonPrint
import com.venson.versatile.log.print.XmlPrint
import java.io.PrintWriter
import java.io.StringWriter

internal object BaseLog {

    /*
    日志类型
     */
    const val V = 0x1
    const val D = 0x2
    const val I = 0x3
    const val W = 0x4
    const val E = 0x5
    const val A = 0x6
    const val JSON = 0x7
    const val XML = 0x8

    /*
    类型
     */
    private const val EXTENSION_JAVA = ".java"
    private const val EXTENSION_KT = ".kt"

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
        Gson()
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
            getThrowableStackTrace(msg)
        } else {
            try {
                isObject = true
                gson.toJson(msg)
            } catch (e: Exception) {
                isObject = false
                msg.toString()
            }
        }
        /*
        打印日志
         */
        val headContent = headString ?: ""
        val msgContent = content ?: ""
        if (VLog.printLogEnable()) {
            when {
                type == JSON || isObject -> {
                    JsonPrint.print(type, tag, headContent, msgContent)
                }
                type == XML -> {
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
            className = it[0].toString()
            headString = "[ ($className:${it[1]})#${it[2]} ] "
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
     * 获取报错信息
     */
    private fun getThrowableStackTrace(tr: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        tr.printStackTrace(pw)
        pw.flush()
        val message = sw.toString()
        val traceString = message.split("\\n\\t").toTypedArray()
        val sb = StringBuilder()
        sb.append("\n")
        for (trace in traceString) {
            sb.append(trace).append("\n")
        }
        return sb.toString()
    }

    /**
     * 获取跳转信息
     */
    private fun getTraceInfo(): Array<Any> {
        val stackTrace = Thread.currentThread().stackTrace
        val stackTraceElement = stackTrace[stackTraceIndex]
        var className = stackTraceElement.className
        val lastFileType = if (stackTraceElement.fileName.endsWith(EXTENSION_KT, true)) {
            EXTENSION_KT
        } else {
            EXTENSION_JAVA
        }
        val classNameInfo = className.split(".").toTypedArray()
        if (classNameInfo.isNotEmpty()) {
            className = classNameInfo[classNameInfo.size - 1] + lastFileType
        }
        if (className.contains("$")) {
            className = className.split("$").toTypedArray()[0] + lastFileType
        }
        val methodName = stackTraceElement.methodName
        var lineNumber = stackTraceElement.lineNumber
        if (lineNumber < 0) {
            lineNumber = 0
        }
        return arrayOf(className, lineNumber, methodName)
    }
}