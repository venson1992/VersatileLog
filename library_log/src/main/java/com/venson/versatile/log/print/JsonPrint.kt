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
                isJsonObject(msg) -> {
                    val jsonObject = JSONObject(msg)
                    jsonObject.toString(JSON_INDENT)
                }
                isJsonArray(msg) -> {
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

    /**
     * 判断文本是否为json
     */
    fun isJson(content: String?): Boolean {
        if (isJsonObject(content) || isJsonArray(content)) {
            return true
        }
        return false
    }

    /**
     * 判断文本是否为json
     */
    private fun isJsonObject(content: String?): Boolean {
        if (content?.startsWith("{") == true && content.startsWith("}")) {
            return true
        }
        return false
    }

    /**
     * 判断文本是否为json
     */
    private fun isJsonArray(content: String?): Boolean {
        if (content?.startsWith("[") == true && content.startsWith("]")) {
            if (content.contains("{") && content.contains("}")) {
                return true
            }
        }
        return false
    }
}