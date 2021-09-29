package com.venson.versatile.log.print

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * JSON输出
 */
internal object JsonPrint : BasePrint() {

    override fun parseContent(msg: String): String {
        val content = msg.trim()
        return try {
            when {
                isJsonObject(content) -> {
                    val jsonObject = JSONObject(content)
                    jsonObject.toString(JSON_INDENT)
                }
                isJsonArray(content) -> {
                    val jsonArray = JSONArray(content)
                    jsonArray.toString(JSON_INDENT)
                }
                else -> {
                    content
                }
            }
        } catch (e: JSONException) {
            content
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
        if (content?.startsWith("{") == true && content.endsWith("}")) {
            return true
        }
        return false
    }

    /**
     * 判断文本是否为json
     */
    private fun isJsonArray(content: String?): Boolean {
        if (content?.startsWith("[") == true && content.endsWith("]")) {
            if (content.contains("{") && content.contains("}")) {
                return true
            }
        }
        return false
    }
}