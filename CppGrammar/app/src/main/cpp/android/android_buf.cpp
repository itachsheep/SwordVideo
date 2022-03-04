//
// Created by wei tao on 2022/3/4.
//

#include "android_buf.h"

AndroidBuf::AndroidBuf() {
    buffer_[BUFFER_SIZE] = '\0';
    setp(buffer_, buffer_ + BUFFER_SIZE - 1);
}

int AndroidBuf::flush_buffer() {
    int len = int(pptr() - pbase());
    if (len <= 0)
        return 0;

    if (len <= BUFFER_SIZE)
        buffer_[len] = '\0';

    android_LogPriority t = ANDROID_LOG_INFO;
//    __android_log_write(t, "CppGrammar.", buffer_);
    __android_log_write(t, TAG , buffer_);

//#ifdef ANDROID
//    android_LogPriority t = ANDROID_LOG_INFO;
//    __android_log_write(t, "Native", buffer_);
//#else
//    printf("%s", buffer_);
//#endif
    pbump(-len);
    return len;
}