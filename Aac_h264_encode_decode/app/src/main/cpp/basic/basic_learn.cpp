//
// Created by wei tao on 2022/1/26.
//

#include "basic_learn.h"

jbyteArray modifyByteArray(JNIEnv *env, jobject thiz,
                     jbyteArray data) {
    jbyte *array = env->GetByteArrayElements(data,JNI_FALSE);
    int length = env->GetArrayLength(data);
    LogD(" modifyByteArray length = %d" , length);
    jbyte *res = (jbyte *) "ni hao !!!";
    jbyteArray pcmByte = env->NewByteArray(20);
    env->SetByteArrayRegion(pcmByte,0,10,res);
    return pcmByte;
}

void test_memcpy() {
    simpleLog();
    string s = "taowei 123 ";
    char *source = (char *)s.c_str();
    int len = strlen(source);
    char *out = new char[len];
    memcpy(out,source,len);
    LogD("test_memcpy out = %s",out);
}