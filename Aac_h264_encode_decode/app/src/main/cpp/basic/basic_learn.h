//
// Created by wei tao on 2022/1/26.
//

#ifndef AAC_H264_ENCODE_DECODE_BASIC_LEARN_H
#define AAC_H264_ENCODE_DECODE_BASIC_LEARN_H
#include <string.h>
//#include <string>

#include <jni.h>
using namespace std;
#include "my_log.h"

struct stu {
    char *name;
    int num;
    int age;
    char group;
    float score;
};

jbyteArray modifyByteArray(JNIEnv *env, jobject thiz,
                     jbyteArray data);

void test_memcpy();
void test_struct();
void test_strcat();

#endif //AAC_H264_ENCODE_DECODE_BASIC_LEARN_H
