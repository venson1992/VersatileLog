package com.venson.versatile.log.print

import android.util.Log
import com.venson.versatile.log.VLog
import com.venson.versatile.log.database.LogDatabase

/**
 * 打印接口
 */
internal abstract class BasePrint {

    companion object {

        //输出日志的最大长度
        const val MAX_LENGTH = 3800

        //输出JSON缩进字符长度
        const val JSON_INDENT = 4

        //换行符
        val LINE_SEPARATOR: String = System.getProperty("line.separator")?.toString() ?: "\n"

        //默认空
        const val NULL_TIPS = "Log with null object"
    }

    /**
     * 打印方法
     */
    fun print(type: Int, tag: String?, header: String, msg: String) {
        val content = parseContent(msg)
        /*
        打印日志
         */
        if (VLog.printLogEnable()) {
            printLine(type, tag, true)
            printSub(type, tag, header)
            var index = 0
            val length = content.length
            val countOfSub = length / MAX_LENGTH
            if (countOfSub > 0) {
                for (i in 0 until countOfSub) {
                    val sub = content.substring(index, index + MAX_LENGTH)
                    printSub(type, tag, sub)
                    index += MAX_LENGTH
                }
                printSub(type, tag, content.substring(index, length))
            } else {
                printSub(type, tag, content)
            }
            printLine(type, tag, false)
        }
        /*
        本地化日志
         */
        if (VLog.saveLogEnable()) {
            VLog.applicationContext()?.let {
                LogDatabase.getInstance(it).logDao().insertLog(tag, type, header, content)
            }
        }
    }

    abstract fun parseContent(msg: String): String

    /**
     * 打印分割线
     */
    private fun printLine(type: Int, tag: String?, isTop: Boolean) {
        val msg = if (isTop) {
            "╔═════════════════════════════════════════════════════════════════════════════════════"
        } else {
            "╚═════════════════════════════════════════════════════════════════════════════════════"
        }
        printSub(type, tag, msg)
    }

    /**
     * 打印内容
     */
    private fun printSub(type: Int, tag: String?, sub: String) {
        when (type) {
            VLog.V -> Log.v(tag, sub)
            VLog.I -> Log.i(tag, sub)
            VLog.W -> Log.w(tag, sub)
            VLog.E -> Log.e(tag, sub)
            VLog.A -> Log.wtf(tag, sub)
            else -> Log.d(tag, sub)
        }
    }
}