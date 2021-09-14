package com.venson.versatile.log

object LogEncryptJNI {

    init {
        System.loadLibrary("log-encrypt-lib")
    }

    external fun readEncrypt(packageName:String):String
}