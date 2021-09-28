package com.venson.versatile.log

fun Any?.logV() {
    this ?: return
    BaseLog.printLog(VLog.V, this, null)
}

fun Any?.logV(tag: String) {
    this ?: return
    BaseLog.printLog(VLog.V, this, tag)
}

fun Any?.logD() {
    this ?: return
    BaseLog.printLog(VLog.D, this, null)
}

fun Any?.logD(tag: String) {
    this ?: return
    BaseLog.printLog(VLog.D, this, tag)
}

fun Any?.logI() {
    this ?: return
    BaseLog.printLog(VLog.I, this, null)
}

fun Any?.logI(tag: String) {
    this ?: return
    BaseLog.printLog(VLog.I, this, tag)
}

fun Any?.logW() {
    this ?: return
    BaseLog.printLog(VLog.W, this, null)
}

fun Any?.logW(tag: String) {
    this ?: return
    BaseLog.printLog(VLog.W, this, tag)
}

fun Any?.logE() {
    this ?: return
    BaseLog.printLog(VLog.E, this, null)
}

fun Any?.logE(tag: String) {
    this ?: return
    BaseLog.printLog(VLog.E, this, tag)
}

fun Any?.logA() {
    this ?: return
    BaseLog.printLog(VLog.A, this, null)
}

fun Any?.logA(tag: String) {
    this ?: return
    BaseLog.printLog(VLog.A, this, tag)
}

fun Any?.logJson() {
    this ?: return
    BaseLog.printLog(VLog.JSON, this, null)
}

fun Any?.logJson(tag: String) {
    this ?: return
    BaseLog.printLog(VLog.JSON, this, tag)
}

fun Any?.logXml() {
    this ?: return
    BaseLog.printLog(VLog.XML, this, null)
}

fun Any?.logXml(tag: String) {
    this ?: return
    BaseLog.printLog(VLog.XML, this, tag)
}

fun Throwable?.printStackTraceByVLog() {
    this ?: return
    BaseLog.printLog(VLog.W, this, null)
}