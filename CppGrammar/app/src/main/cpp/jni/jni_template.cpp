//
// Created by wei tao on 2022/3/3.
//
#include <jni.h>
#include <iostream>
#include "android_buf.h"

using namespace std;


template<typename T>
void func(T& t) {
    cout << "In generic version template t = " << t  << endl;
//    LOGD("func In generic version template t = %d ",t);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_myndktest_NativeHelper_testTemplate(JNIEnv *env, jobject thiz) {
    //simpleLog();
//    LOGD("------>  111 ");
    cout.rdbuf( new AndroidBuf());
    int a = 100;
    func(a);
}