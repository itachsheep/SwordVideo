//
// Created by wei tao on 2022/3/3.
//
#include <jni.h>
#include <iostream>
#include "my_log.h"
using namespace std;

template<typename T>
void func(T& t) {
    cout << "In generic version template t = " << t << endl;
    
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_myndktest_NativeHelper_testTemplate(JNIEnv *env, jobject thiz) {
    simpleLog();
    int a = 100;
    func(a);
}