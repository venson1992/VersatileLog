package com.venson.versatile.log.interceptor

import android.os.SystemClock
import android.util.Log
import androidx.annotation.IntDef
import com.venson.versatile.log.VLog
import com.venson.versatile.log.appendDataAndLine
import com.venson.versatile.log.print.DefaultPrint
import com.venson.versatile.log.print.HTTPPrint
import com.venson.versatile.log.print.JsonPrint
import com.venson.versatile.log.print.XmlPrint
import okhttp3.*
import okio.Buffer

/**
 * 针对okhttp的日志拦截器
 * @param level @Level
 */
class LogInterceptor(
    val tag: String = DEFAULT_TAG,
    @Level val level: Int = LEVEL_ALL
) : Interceptor {

    companion object {
        private const val DEFAULT_TAG = "VLOG_HTTP_TAG"

        const val LEVEL_NONE = 0x00
        const val LEVEL_ALL = 0x01
        const val LEVEL_REQUEST = 0x02
        const val LEVEL_RESPONSE = 0x03
    }

    @IntDef(LEVEL_NONE, LEVEL_ALL, LEVEL_REQUEST, LEVEL_RESPONSE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Level

    override fun intercept(chain: Interceptor.Chain): Response? {
        /*
        未开启打印和本地化
         */
        if (!VLog.printLogEnable() && !VLog.saveLogEnable()) {
            return chain.proceed(chain.request())
        }
        val startTime = SystemClock.elapsedRealtime()
        val request = try {
            chain.request()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: let {
            return null
        }
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: let {
            return null
        }
        val endTime = SystemClock.elapsedRealtime()
        val duration = endTime - startTime
        val requestContent = StringBuilder()
        /*
        method url
         */
        try {
            requestContent.appendDataAndLine(
                request.method().uppercase() + "  " + request.url().toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*
        headers
         */
        try {
            request.headers().let { headers ->
                headers.names().forEach { key ->
                    try {
                        requestContent.appendDataAndLine("$key : ${headers[key]}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*
        params
         */
        val requestBody = request.body()
        readRequestParamString(requestBody)?.let {
            if (it.isNotEmpty()) {
                requestContent.appendDataAndLine("Params : $it")
            }
        }
        /*
        response
         */
        val responseBody = response.body()
        var responseString: String? = null
        if (null != responseBody) {
            val type = responseBody.contentType()
            responseString = if (isPlainText(type)) {
                try {
                    response.peekBody(Long.MAX_VALUE).string().let {
                        if (isPlainText(type, "json")
                            || isPlainText(type, "plain")
                            || isPlainText(type, "text")
                            || isPlainText(type, "form")
                        ) {
                            JsonPrint.parseContent(it)
                        } else if (isPlainText(type, "xml")
                            || isPlainText(type, "html")
                        ) {
                            XmlPrint.parseContent(it)
                        } else {
                            DefaultPrint.parseContent(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.getStackTraceString(e)
                }
            } else {
                "other-type=" + responseBody.contentType()
            }
        }
        printLog(requestContent.toString(), responseString, duration)
        saveLog(requestContent.toString(), responseString, startTime, endTime, duration)
        return response
    }

    /**
     * 读取请求参数
     *
     * @param requestBody 请求体
     * @return 文本
     */
    private fun readRequestParamString(requestBody: RequestBody?): String? {
        return if (requestBody is MultipartBody) { //判断是否有文件
            val sb = java.lang.StringBuilder()
            val parts = requestBody.parts()
            var partBody: RequestBody
            var i = 0
            val size = parts.size
            while (i < size) {
                partBody = parts[i].body()
                if (sb.isNotEmpty()) {
                    sb.append(",")
                }
                if (isPlainText(partBody.contentType())) {
                    sb.append(readContent(partBody))
                } else {
                    sb.append("other-param-type=").append(partBody.contentType())
                }
                i++
            }
            sb.toString()
        } else {
            readContent(requestBody)
        }
    }

    /**
     * 是否输出日志
     *
     * @param mediaType 类型
     * @return Boolean
     */
    private fun isPlainText(mediaType: MediaType?): Boolean {
        mediaType?.toString()?.let {
            return it.contains("plain", true)
                    || it.contains("text", true)
                    || it.contains("html", true)
                    || it.contains("form", true)
                    || it.contains("json", true)
                    || it.contains("xml", true)
        }
        return false
    }

    /**
     * 是否输出日志
     *
     * @param mediaType 类型
     * @return Boolean
     */
    private fun isPlainText(mediaType: MediaType?, needType: String): Boolean {
        mediaType?.toString()?.let {
            return it.contains(needType, true)
        }
        return false
    }

    /**
     * 读取文本
     *
     * @param body 内容
     * @return 文本
     */
    private fun readContent(body: RequestBody?): String? {
        if (body == null) {
            return ""
        }
        val buffer = Buffer()
        try {
            if (body.contentLength() <= 2 * 1024 * 1024) { //小于2m
                body.writeTo(buffer)
            } else {
                return "content is more than 2M"
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return buffer.readUtf8()
    }

    /**
     * 打印日志
     */
    private fun printLog(request: String?, response: String?, duration: Long) {
        if (!VLog.printLogEnable()) {
            return
        }
        val message = StringBuilder()
        message.append("----------Request Start----------")
        if (level == LEVEL_ALL || level == LEVEL_REQUEST) {
            message.appendDataAndLine(request)
        }
        if (level == LEVEL_ALL || level == LEVEL_RESPONSE) {
            message.appendDataAndLine("Response Body : ")
                .appendDataAndLine(response)
        }
        message.appendDataAndLine("Time : $duration ms")
        message.appendDataAndLine("----------Request End----------")
        HTTPPrint.print(VLog.HTTP, tag, "", message.toString())
    }

    /**
     * 本地化日志
     */
    private fun saveLog(
        request: String?,
        response: String?,
        startTime: Long,
        endTime: Long,
        duration: Long
    ) {
        if (!VLog.saveLogEnable()) {
            return
        }
    }
}