//
// Created by wei tao on 2022/3/4.
//

#ifndef CPPGRAMMAR_ANDROID_BUF_H
#define CPPGRAMMAR_ANDROID_BUF_H
#include <iostream>
#include <streambuf>
#include <android/log.h>
#include "my_log.h"

class AndroidBuf: public std::streambuf {
    enum {
        BUFFER_SIZE = 255,
    };

public:
    AndroidBuf();
    ~AndroidBuf() {
        sync();
    }


protected:
    virtual int_type overflow(int_type c) {
        if (c != EOF) {
            *pptr() = c;
            pbump(1);
        }
        flush_buffer();
        return c;
    }

    virtual int sync() {
        flush_buffer();
        return 0;
    }

private:
    int flush_buffer();

private:
    char buffer_[BUFFER_SIZE + 1];
};


#endif //CPPGRAMMAR_ANDROID_BUF_H
