//
// Created by wei tao on 2022/1/24.
//

#ifndef AAC_H264_ENCODE_DECODE_AAC_CTRL_H
#define AAC_H264_ENCODE_DECODE_AAC_CTRL_H

#include <jni.h>
#include "my_log.h"

class aac_ctrl {

};

extern int initWithADTSFormat();

extern jbyteArray decode(JNIEnv *jniEnv,jobject jobje,
        jbyteArray aacByte, jint byteSize);

extern void destroy();

#endif //AAC_H264_ENCODE_DECODE_AAC_CTRL_H
