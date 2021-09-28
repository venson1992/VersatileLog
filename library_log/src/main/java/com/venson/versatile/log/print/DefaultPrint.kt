package com.venson.versatile.log.print

/**
 * 基础输出
 */
internal object DefaultPrint : BasePrint() {

    override fun parseContent(msg: String): String {
        return msg
    }

}