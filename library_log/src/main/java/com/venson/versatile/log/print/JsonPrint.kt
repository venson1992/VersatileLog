package com.venson.versatile.log.print

import com.venson.versatile.log.VLog
import com.venson.versatile.log.database.LogDatabase
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * JSON输出
 */
internal object JsonPrint : BasePrint() {

    override fun print(type: Int, tag: String?, header: String, msg: String) {
        var message: String = try {
            when {
                msg.startsWith("{") -> {
                    val jsonObject = JSONObject(msg)
                    jsonObject.toString(JSON_INDENT)
                }
                msg.startsWith("[") -> {
                    val jsonArray = JSONArray(msg)
                    jsonArray.toString(JSON_INDENT)
                }
                else -> {
                    msg
                }
            }
        } catch (e: JSONException) {
            msg
        }
        printLine(tag, true)
        message = header + LINE_SEPARATOR + message
        message.split(LINE_SEPARATOR).forEach {
            printSub(type, tag, "║ $it")
        }
        printLine(tag, false)
        if (VLog.saveLogEnable()) {
            VLog.applicationContext()?.let {
                LogDatabase.getInstance(it).logDao().insertLog(tag, type, message)
            }
        }
    }
}