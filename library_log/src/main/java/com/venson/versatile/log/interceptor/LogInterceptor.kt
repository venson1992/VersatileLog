package com.venson.versatile.log.interceptor

import android.os.SystemClock
import android.util.Log
import androidx.annotation.IntDef
import com.venson.versatile.log.VLog
import com.venson.versatile.log.appendDataAndLine
import com.venson.versatile.log.database.LogDatabase
import com.venson.versatile.log.print.DefaultPrint
import com.venson.versatile.log.print.HTTPPrint
import com.venson.versatile.log.print.JsonPrint
import com.venson.versatile.log.print.XmlPrint
import okhttp3.*
import okio.Buffer
import java.net.URLDecoder

/**
 * 针对okhttp的日志拦截器
 * @param tag tag
 * @param level @Level
 * @param hardContentType @ContentType default默认解析，否则按预设格式解析
 */
class LogInterceptor(
    val tag: String = DEFAULT_TAG,
    @Level val level: Int = LEVEL_ALL,
    @ContentType val hardContentType: Int = TYPE_DEFAULT
) : Interceptor {

    companion object {
        private const val DEFAULT_TAG = "VLOG_HTTP_TAG"

        const val LEVEL_NONE = 0x00
        const val LEVEL_ALL = 0x01
        const val LEVEL_REQUEST = 0x02
        const val LEVEL_RESPONSE = 0x03

        const val TYPE_DEFAULT = 0x00
        const val TYPE_XML = 0x01
        const val TYPE_JSON = 0x02
    }

    @IntDef(LEVEL_NONE, LEVEL_ALL, LEVEL_REQUEST, LEVEL_RESPONSE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Level

    @IntDef(TYPE_DEFAULT, TYPE_XML, TYPE_JSON)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ContentType

    override fun intercept(chain: Interceptor.Chain): Response? {
        /*
        未开启打印和本地化
         */
        if (!VLog.printLogEnable() && !VLog.saveLogEnable()) {
            return chain.proceed(chain.request())
        }
        /*
        request
         */
        val startTime = SystemClock.elapsedRealtime()
        val request = chain.request()
        val requestContent = StringBuilder()
        /*
        method url
         */
        var method = ""
        var url = ""
        try {
            method = request.method().uppercase()
            url = request.url().toString()
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
        printRequest(requestContent.toString())
        val columnId = saveRequest(url, method, requestContent.toString(), startTime)
        /*
        response
         */
        val response = chain.proceed(request)
        val endTime = SystemClock.elapsedRealtime()
        val duration = endTime - startTime
        /*
        response
         */
        val responseBody = response.body()
        var responseString: String? = null
        val contentType = responseBody?.contentType()?.toString() ?: "contentType un-known"
        if (null != responseBody) {
            responseString = if (isPlainText(contentType)) {
                try {
                    response.peekBody(Long.MAX_VALUE).string().let {
                        if (hardContentType == TYPE_JSON) {
                            JsonPrint.parseContent(it)
                        } else if (hardContentType == TYPE_XML) {
                            XmlPrint.parseContent(it)
                        } else if (isPlainText(contentType, "xml")
                            || isPlainText(contentType, "html")
                        ) {
                            XmlPrint.parseContent(it)
                        } else if (isPlainText(contentType, "json")
                            || isPlainText(contentType, "plain")
                            || isPlainText(contentType, "text")
                            || isPlainText(contentType, "form")
                        ) {
                            JsonPrint.parseContent(it)
                        } else {
                            DefaultPrint.parseContent(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.getStackTraceString(e)
                }
            } else {
                "other-type=$contentType"
            }
        }
        printResponse(requestContent.toString(), contentType, responseString, duration)
        updateResponse(
            columnId,
            url,
            method,
            requestContent.toString(),
            startTime,
            contentType,
            responseString,
            endTime,
            duration
        )
        return response
    }

    /**
     * 读取请求参数
     *
     * @param requestBody 请求体
     * @return 文本
     */
    private fun readRequestParamString(requestBody: RequestBody?): String? {
        return when (requestBody) {
            is MultipartBody -> { //判断是否有文件
                val sb = StringBuilder()
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                val postParams = buffer.readUtf8()
                val splitNames = postParams.split("\n")
                val names = mutableListOf<String>()
                splitNames.forEach { splitName ->
                    if (splitName.contains("Content-Disposition")) {
                        names.add(
                            splitName
                                .replace(
                                    "Content-Disposition: form-data; name=", ""
                                )
                                .replace("\"", "")
                                .replace("\r", "")
                        )
                    }
                }
                requestBody.parts().forEachIndexed { index, part ->
                    val partBody = part.body()
                    val type = partBody.contentType()?.type() ?: ""
                    if (type.contains("image", true)
                        || type.contains("video", true)
                        || type.contains("audio", true)
                        || type.contains("application", true)
                    ) {
                        return@forEachIndexed
                    }
                    val key: String = try {
                        names[index]
                    } catch (e: Exception) {
                        return@forEachIndexed
                    }
                    if (key.startsWith("application")) {
                        return@forEachIndexed
                    }
                    val partBuffer = Buffer()
                    partBody.writeTo(partBuffer)
                    val value = partBuffer.readUtf8().let {
                        try {
                            URLDecoder.decode(it, "UTF-8")
                        } catch (e: Exception) {
                            it
                        }
                    }
                    if (sb.isNotEmpty()) {
                        sb.append("&")
                    }
                    sb.append("$key=$value")
                }
                sb.toString()
            }
            is FormBody -> {
                val sb = StringBuilder()
                val size = requestBody.size()
                for (index in 0 until size) {
                    try {
                        val key = requestBody.name(index) ?: ""
                        val value = requestBody.value(index)
                        if (key.trim().isNotEmpty()) {
                            if (sb.isNotEmpty()) {
                                sb.append("&")
                            }
                            sb.append("$key=$value")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                sb.toString()
            }
            else -> {
                readContent(requestBody).let {
                    try {
                        URLDecoder.decode(it, "UTF-8")
                    } catch (e: Exception) {
                        it
                    }
                }
            }
        }
    }

    /**
     * 预设格式
     *
     * @param mediaType 类型
     * @return Boolean
     */
    private fun isPlainText(mediaType: String): Boolean {
        return mediaType.trim().isEmpty()
                || mediaType.contains("plain", true)
                || mediaType.contains("text", true)
                || mediaType.contains("html", true)
                || mediaType.contains("form", true)
                || mediaType.contains("json", true)
                || mediaType.contains("xml", true)
    }

    /**
     * 是否预设格式
     *
     * @param mediaType 类型
     * @return Boolean
     */
    private fun isPlainText(mediaType: String, needType: String): Boolean {
        return mediaType.contains(needType, true)
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
     * 打印请求体
     */
    private fun printRequest(request: String?) {
        if (!VLog.printLogEnable() || (level != LEVEL_ALL && level != LEVEL_REQUEST)) {
            return
        }
        val message = StringBuilder()
        message.append(" ")
        message.appendDataAndLine("----------Request Start----------")
        message.appendDataAndLine(request)
        message.appendDataAndLine("----------Request End----------")
        HTTPPrint.print(VLog.HTTP, tag, "", message.toString())
    }

    /**
     * 打印日志
     */
    private fun printResponse(
        request: String?,
        contentType: String,
        response: String?,
        duration: Long
    ) {
        if (!VLog.printLogEnable() || level == LEVEL_NONE) {
            return
        }
        val message = StringBuilder()
        message.append(" ")
        message.appendDataAndLine("----------Response Start----------")
        if (level == LEVEL_ALL || level == LEVEL_REQUEST) {
            message.appendDataAndLine(request)
        }
        if (level == LEVEL_ALL || level == LEVEL_RESPONSE) {
            message.appendDataAndLine("Response Body ( Content-Type = $contentType ): ")
                .appendDataAndLine(response)
        }
        message.appendDataAndLine("Time : $duration ms")
        message.appendDataAndLine("----------Response End----------")
        HTTPPrint.print(VLog.HTTP, tag, "", message.toString())
    }

    private fun saveRequest(
        url: String?,
        method: String?,
        request: String?,
        startTime: Long,
    ): Long {
        if (!VLog.saveLogEnable()) {
            return -1L
        }
        VLog.applicationContext()?.let {
            return LogDatabase.getInstance(it).httpLogDao().insertLog(
                url,
                method,
                request,
                "",
                null,
                startTime,
                0,
                0
            )
        }
        return -1L
    }

    /**
     * 本地化日志
     */
    private fun updateResponse(
        columnId: Long,
        url: String?,
        method: String?,
        request: String?,
        startTime: Long,
        contentType: String,
        response: String?,
        endTime: Long,
        duration: Long
    ) {
        if (!VLog.saveLogEnable()) {
            return
        }
        VLog.applicationContext()?.let {
            LogDatabase.getInstance(it).httpLogDao()
                .updateLog(
                    columnId,
                    url,
                    method,
                    request,
                    contentType,
                    response,
                    startTime,
                    endTime,
                    duration
                )
        }
    }
}