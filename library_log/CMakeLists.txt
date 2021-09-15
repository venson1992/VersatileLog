#指定需要CMAKE的最小版本
cmake_minimum_required(VERSION 3.4.1)

#当前cmakelists.txt所在目录的所有.c .cpp源文件
AUX_SOURCE_DIRECTORY(. SRC_LIST)

##C 的编译选项是 CMAKE_C_FLAGS
## 指定编译参数，可选
SET(CMAKE_CXX_FLAGS "-std=c++11 -fexceptions -frtti -fpermissive -Wno-error=format-security -Wno-error=pointer-sign")
set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -s")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -s")
set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -s")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -s")

#设置生成的so动态库最后输出的路径
set(jnilibs "${PROJECT_SOURCE_DIR}/libs")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${jnilibs}/${ANDROID_ABI})

add_library( # Specifies the name of the library.
        log-encrypt-lib

        # Sets the library as a shared library.  STATIC  SHARED
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/MD5.cpp
        src/main/cpp/log-encrypt-lib.cpp)

#找到预编译库log_lib 并link到我们的动态库中
find_library(
        log-lib
        log)

#制定目标库.
target_link_libraries(
        log-encrypt-lib
        ${log-lib})

if (${ANDROID_ABI} STREQUAL "x86_64")
    include_directories(${ANDROID_SYSROOT}/usr/include/x86_64-linux-android)
elseif (${ANDROID_ABI} STREQUAL "x86")
    include_directories(${ANDROID_SYSROOT}/usr/include/i686-linux-android)
elseif (${ANDROID_ABI} STREQUAL "arm64-v8a")
    include_directories(${ANDROID_SYSROOT}/usr/include/aarch64-linux-android)
elseif (${ANDROID_ABI} STREQUAL "armeabi-v7a")
    include_directories(${ANDROID_SYSROOT}/usr/include/arm-linux-androideabi)
else ()
    include_directories(${ANDROID_SYSROOT}/usr/include/arm-linux-androideabi)
endif ()