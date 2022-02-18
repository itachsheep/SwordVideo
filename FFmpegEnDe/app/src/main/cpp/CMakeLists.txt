# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

set(jni ${CMAKE_SOURCE_DIR}/jni)
set(utils ${CMAKE_SOURCE_DIR}/utils)
set(audioEncode ${CMAKE_SOURCE_DIR}/audioencode)
set(audioDecode ${CMAKE_SOURCE_DIR}/audiodecode)

#三方
set(THIRD_PARTY_ROOT ${CMAKE_SOURCE_DIR}/thirdparty)
set(FFMPEG_PATH ${THIRD_PARTY_ROOT}/ffmpeg)
set(FDK-AAC_PATH ${THIRD_PARTY_ROOT}/fdk_aac)
set(X264_PATH ${THIRD_PARTY_ROOT}/x264)


include_directories(
        ${jni}
        ${utils}
        ${audioEncode}
        ${audioDecode}
        ${FFMPEG_PATH}/include
)

file(GLOB src_cpp
        ${jni}/*.cpp
        ${audioEncode}/*.cpp
        ${audioDecode}/*.cpp
        ${audioEncode}/audio_decoder.cpp
)

project("ffmpeg")

add_library(use_ffmpeg
             SHARED
             ${src_cpp})

#编解码库
add_library(avcodec STATIC IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PATH}/libs/${CMAKE_ANDROID_ARCH_ABI}/libavcodec.a)
#文件格式和封装协议
add_library(avformat STATIC IMPORTED)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PATH}/libs/${CMAKE_ANDROID_ARCH_ABI}/libavformat.a)
#重采样
add_library(swresample STATIC IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PATH}/libs/${CMAKE_ANDROID_ARCH_ABI}/libswresample.a)

#工具包
add_library(avutil STATIC IMPORTED)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_PATH}/libs/${CMAKE_ANDROID_ARCH_ABI}/libavutil.a)

#fdk-aac
add_library(fdkaac STATIC IMPORTED)
set_target_properties(fdkaac PROPERTIES IMPORTED_LOCATION
        ${FDK-AAC_PATH}/libs/${CMAKE_ANDROID_ARCH_ABI}/libfdk-aac.a)

#libx264
add_library(x264 STATIC IMPORTED)
set_target_properties(x264 PROPERTIES IMPORTED_LOCATION
        ${X264_PATH}/libs/${CMAKE_ANDROID_ARCH_ABI}/libx264.a)


find_library(log-lib
        log
        android)

target_link_libraries( # Specifies the target library.
        use_ffmpeg
        avformat
        avcodec
        swresample
        avutil
        fdkaac
        x264
        z #[[不加 z 会导致报未定义的错]]
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )