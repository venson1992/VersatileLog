package com.venson.versatile.log.print

/**
 * 基础输出
 */
internal object DefaultPrint : BasePrint() {

    override fun print(type: Int, tag: String?, header: String, msg: String) {
        val message = header + msg
        var index = 0
        val length = message.length
        val countOfSub = length / MAX_LENGTH
        if (countOfSub > 0) {
            for (i in 0 until countOfSub) {
                val sub = message.substring(index, index + MAX_LENGTH)
                printSub(type, tag, sub)
                index += MAX_LENGTH
            }
            printSub(type, tag, message.substring(index, length))
        } else {
            printSub(type, tag, message)
        }
    }

}