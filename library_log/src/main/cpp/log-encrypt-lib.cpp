//
// Created by Venson on 2021/9/14 014.
//

#include "log-encrypt-lib.h"
#include "MD5.h"
#include <jni.h>
#include <cmath>
#include <map>
#include <cstring>
#include <cstring>
#include <cstdio>
#include <ctime>
#include <vector>
#include <strstream>
#include <android/log.h>
#include <string>
#include <iconv.h>
#include <cstdlib>

using namespace std;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_venson_versatile_log_LogEncryptJNI_readEncrypt(JNIEnv *env, jobject thiz,
                                                        jstring package_name) {
    const char *originStr;
    //将jstring转化成char *类型
    originStr = env->GetStringUTFChars(package_name, nullptr);
    MD5 md5 = MD5(originStr);
    std::string md5Result = md5.hexdigest();
    //将char *类型转化成jstring返回给Java层
    return env->NewStringUTF(md5Result.c_str());
}