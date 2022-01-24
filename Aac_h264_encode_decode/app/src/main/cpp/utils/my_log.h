//
// Created by wei tao on 2022/1/11.
//

#ifndef CPP_LRN_MY_LOG_H
#define CPP_LRN_MY_LOG_H
#include <android/log.h>
#include <string>

#define TAG "MyAV."
#define LogD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define LogE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

#define simpleLog() LogD("%s --> %s",__FILE_NAME__, __FUNCTION__)

typedef unsigned char byte;

#endif //CPP_LRN_MY_LOG_H
