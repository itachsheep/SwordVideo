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

    stu *stu4 = new stu;
    stu4->name = "小明";
    stu4->num = 201038;
    LogD("test_struct false = %d ", false);
    LogD("test_struct true = %d ", true);
}

void test_strcat() {
    simpleLog();
    char *path = "/sdcard/android/data/files/";
    const char *src = "my_record.aac";
    /**
     * dest为起始字符串，src为拼接在后面的字符串
     *  这样做确实成功将两个字符串拼接起来了，
     *  但是这导致内存操作错误，因为strcat只是将src加到了dest的后面，
     *  但是dest没有多余的容量来容纳这些数据
     *  虽然我们编译过去，也得到了正确的结果，
     *  但每次我们的数据都会超出所属内存，最终导致异常崩溃！
     */
    //todo: char *out = strcat(path,"my_record.aac");
    /**
     * 正确的拼接方法如下
     */
    char *out = (char *)malloc(strlen(path) + strlen(src));
//    memset(out,0,strlen(path) + strlen(src));
//    strcat(out,path);
    strcpy(out,path);
    strcat(out,src);
    LogD("test_strcat out len = %d, out = %s",strlen(out),out);
}