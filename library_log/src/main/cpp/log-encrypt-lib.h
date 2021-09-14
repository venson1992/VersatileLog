//
// Created by Venson on 2021/9/14 014.
//

#ifndef VERSATILELOG_LOG_ENCRYPT_LIB_H
#define VERSATILELOG_LOG_ENCRYPT_LIB_H

#include <jni.h>

extern "C" jstring JNICALL
Java_com_venson_versatile_log_LogEncryptJNI_readEncrypt(JNIEnv *env, jobject thiz,
                                                        jstring package_name);

#endif //VERSATILELOG_LOG_ENCRYPT_LIB_H