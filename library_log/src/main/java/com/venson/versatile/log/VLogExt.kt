package com.venson.versatile.log

fun Any?.logV() {
    this ?: return
    BaseLog.printLog(BaseLog.V, this, null)
}

fun Any?.logV(tag: String) {
    this ?: return
    BaseLog.printLog(BaseLog.V, this, tag)
}

fun Any?.logD() {
    this ?: return
    BaseLog.printLog(BaseLog.D, this, null)
}

fun Any?.logD(tag: String) {
    this ?: return
    BaseLog.printLog(BaseLog.D, this, tag)
}

fun Any?.logI() {
    this ?: return
    BaseLog.printLog(BaseLog.I, this, null)
}

fun Any?.logI(tag: String) {
    this ?: return
    BaseLog.printLog(BaseLog.I, this, tag)
}

fun Any?.logW() {
    this ?: return
    BaseLog.printLog(BaseLog.W, this, null)
}

fun Any?.logW(tag: String) {
    this ?: return
    BaseLog.printLog(BaseLog.W, this, tag)
}

fun Any?.logE() {
    this ?: return
    BaseLog.printLog(BaseLog.E, this, null)
}

fun Any?.logE(tag: String) {
    this ?: return
    BaseLog.printLog(BaseLog.E, this, tag)
}

fun Any?.logA() {
    this ?: return
    BaseLog.printLog(BaseLog.A, this, null)
}

fun Any?.logA(tag: String) {
    this ?: return
    BaseLog.printLog(BaseLog.A, this, tag)
}

fun Any?.logJson() {
    this ?: return
    BaseLog.printLog(BaseLog.JSON, this, null)
}

fun Any?.logJson(tag: String) {
    this ?: return
    BaseLog.printLog(BaseLog.JSON, this, tag)
}

fun Any?.logXml() {
    this ?: return
    BaseLog.printLog(BaseLog.XML, this, null)
}

fun Any?.logXml(tag: String) {
    this ?: return
    BaseLog.printLog(BaseLog.XML, this, tag)
}