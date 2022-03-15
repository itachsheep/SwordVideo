//
// Created by wei tao on 2022/3/15.
//

#ifndef CPPGRAMMAR_SIMPLE_LOG_H
#define CPPGRAMMAR_SIMPLE_LOG_H
#include <android/log.h>


#define TAG "CppGrammar."
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

#define simpleLog() LOGD("%s --> %s",__FILE_NAME__, __FUNCTION__)



#endif //CPPGRAMMAR_SIMPLE_LOG_H
