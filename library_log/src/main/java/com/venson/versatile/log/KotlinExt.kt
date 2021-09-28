package com.venson.versatile.log

fun StringBuilder?.appendDataAndLine(msg: String?): StringBuilder? {
    msg ?: return this
    this ?: return this
    if (length > 0) {
        append("\n")
    }
    append(msg)
    return this
}