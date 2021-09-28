package com.venson.versatile.log.print

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * JSON输出
 */
internal object JsonPrint : BasePrint() {

    override fun parseContent(msg: String): String {
        return try {
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
    }
}