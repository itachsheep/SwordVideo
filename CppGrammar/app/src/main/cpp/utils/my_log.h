//
// Created by wei tao on 2022/1/11.
//

#ifndef CPP_LRN_MY_LOG_H
#define CPP_LRN_MY_LOG_H
#include <android/log.h>
#include <string>

#define TAG "CppGrammar."
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

#define simpleLog() LOGD("%s --> %s",__FILE_NAME__, __FUNCTION__)


static inline long long currentTimeMills(){
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return (long long)tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

typedef unsigned char byte;

#endif //CPP_LRN_MY_LOG_H
