package com.venson.versatile.log.print

import android.util.Log
import com.venson.versatile.log.BaseLog

/**
 * 打印接口
 */
internal abstract class BasePrint {

    companion object {

        //输出日志的最大长度
        const val MAX_LENGTH = 4000

        //输出JSON缩进字符长度
        const val JSON_INDENT = 4

        //换行符
        val LINE_SEPARATOR: String = System.getProperty("line.separator")?.toString() ?: "\n"

        //默认空
        const val NULL_TIPS = "Log with null object"
    }

    abstract fun print(type: Int, tag: String?, header: String, msg: String)

    protected fun printLine(tag: String?, isTop: Boolean) {
        val msg = if (isTop) {
            "╔═════════════════════════════════════════════════════════════════════════════════════"
        } else {
            "╚═════════════════════════════════════════════════════════════════════════════════════"
        }
        printSub(BaseLog.D, tag, msg)
    }

    protected fun printSub(type: Int, tag: String?, sub: String) {
        when (type) {
            BaseLog.V -> Log.v(tag, sub)
            BaseLog.I -> Log.i(tag, sub)
            BaseLog.W -> Log.w(tag, sub)
            BaseLog.E -> Log.e(tag, sub)
            BaseLog.A -> Log.wtf(tag, sub)
            else -> Log.d(tag, sub)
        }
    }
}