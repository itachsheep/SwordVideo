//
// Created by wei tao on 2022/1/26.
//

#ifndef AAC_H264_ENCODE_DECODE_BASIC_LEARN_H
#define AAC_H264_ENCODE_DECODE_BASIC_LEARN_H
//#include <string.h>
#include <string>

#include <jni.h>
using namespace std;
#include "my_log.h"

jbyteArray modifyByteArray(JNIEnv *env, jobject thiz,
                     jbyteArray data);

void test_memcpy();

#endif //AAC_H264_ENCODE_DECODE_BASIC_LEARN_H
