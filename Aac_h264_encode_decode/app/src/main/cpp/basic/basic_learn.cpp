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

void test_struct() {
    simpleLog();
    struct stu stu1;
    stu1.name = "taowei";
    stu1.num = 20103632;

    struct stu stu2 = {"liming",20103212,
            18,'A',140.5f};
    //读取结构体成员的值
    LogD("%s的学号是%d，年龄是%d，在%c组，今年的成绩是%.1f！\n",
            stu1.name,stu1.num, stu1.age, stu1.group, stu1.score);
    LogD("%s的学号是%d，年龄是%d，在%c组，今年的成绩是%.1f！\n",
            stu2.name,stu2.num, stu2.age, stu2.group, stu2.score);
    struct stu stu3 = {"liLei",201};
    LogD("%s的学号是%d，年龄是%d，在%c组，今年的成绩是%.1f！\n",
         stu3.name,stu3.num, stu3.age, stu3.group, stu3.score);
}