# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

project("aac_h264_encode_decode")

set(THIRD_PARTY_ROOT ${CMAKE_SOURCE_DIR}/thirdparty)
set(aac ${CMAKE_SOURCE_DIR}/aac)
set(utils ${CMAKE_SOURCE_DIR}/utils)
set(basic ${CMAKE_SOURCE_DIR}/basic)
set(jni ${CMAKE_SOURCE_DIR}/jni)

include_directories(
        ${THIRD_PARTY_ROOT}/fdk_aac/include
        ${aac}
        ${basic}
        ${aac}/decode
        ${utils}
        ${jni}
)

file(GLOB src_cpp
        ${aac}/*.cpp
        ${aac}/decode/*.cpp
        ${basic}/*.cpp
        ${CMAKE_SOURCE_DIR}/native-lib.cpp
        ${jni}/*.cpp
)

add_library(thirdparty_aac STATIC IMPORTED)
set_target_properties(thirdparty_aac PROPERTIES IMPORTED_LOCATION
        ${THIRD_PARTY_ROOT}/fdk_aac/libs/${CMAKE_ANDROID_ARCH_ABI}/libfdk-aac.a)

add_library(aac_h264
        SHARED
        ${src_cpp}
)

find_library(log-lib
        log
        android
)

target_link_libraries(
        aac_h264
        thirdparty_aac
        ${log-lib}
)