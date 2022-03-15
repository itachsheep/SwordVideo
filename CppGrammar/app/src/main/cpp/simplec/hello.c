//
// Created by wei tao on 2022/3/15.
//

#include <stdio.h>
#include "simple_log.h"
#include "hello2.h"
//
char* printHello() {
    char *s = "hello world";
    printf("hello world");
    LOGD("printHello ----->");

    LOGD("call cpp function from c ----->");
    printHello_cpp();
    return s;
}