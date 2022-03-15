//
// Created by wei tao on 2022/1/11.
//

#ifndef CPP_LRN_MY_LOG_H
#define CPP_LRN_MY_LOG_H

#include "simple_log.h"
#include <string>

static inline long long currentTimeMills(){
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return (long long)tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

typedef unsigned char byte;

#endif //CPP_LRN_MY_LOG_H
