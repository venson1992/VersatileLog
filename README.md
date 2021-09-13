# VersatileLog 日志框架

- 支持堆栈信息输出
- 支持json、xml格式化输出
- 支持对象序列化输出

## 全局配置

```kotlin
VLog.traceLogEnable(BuildConfig.DEBUG)//是否抓取堆栈信息，默认值为BuildConfig.DEBUG
    .printLogEnable(BuildConfig.DEBUG)//是否打印日志信息，默认值为BuildConfig.DEBUG
    .saveLogEnable(BuildConfig.DEBUG)//是否保存日志信息，默认值为BuildConfig.DEBUG
    .globalTag("YOURS_GLOBAL_TAG")//全局TAG，默认值为null
```

## 使用

```kotlin
VLog.d()
VLog.d("msg")
VLog.d("tag", "msg")
"msg".logD()
"msg".logD("tag")

VLog.a()
VLog.a("msg")
VLog.a("tag", "msg")
"msg".logA()
"msg".logA("tag")

VLog.v()
VLog.v("msg")
VLog.v("tag", "msg")
"msg".logV()
"msg".logV("tag")

VLog.w()
VLog.w("msg")
VLog.w("tag", "msg")
"msg".logW()
"msg".logW("tag")

VLog.i()
VLog.i("msg")
VLog.i("tag", "msg")
"msg".logI()
"msg".logI("tag")

VLog.e()
VLog.e("msg")
VLog.e("tag", "msg")
"msg".logE()
"msg".logE("tag")

VLog.json("msg")
VLog.json("tag", "msg")
"msg".logJson()
"msg".logJson("tag")

VLog.xml("msg")
VLog.xml("tag", "msg")
"msg".logXml()
"msg".logXml("tag")
```