package com.venson.versatile.log.print

internal object HTTPPrint : BasePrint() {

    override fun parseContent(msg: String): String {
        return msg
    }
}