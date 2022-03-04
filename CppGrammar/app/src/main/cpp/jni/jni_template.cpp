//
// Created by wei tao on 2022/3/3.
//
#include <jni.h>
#include <iostream>
#include "android_buf.h"

using namespace std;


template<typename T>
int compare(T& left, T& right) {
    if(left < right) {
        return  -1;
    } else if( left > right) {
        return 1;
    } else {
        return 0;
    }
}

template<typename T>
void func(T& t) {
    cout << "In generic version template t = " << t  << endl;
}

template<typename T>
void func(T *t) {
    cout << "In pointer version template *t = " << *t << endl;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_tao_myndktest_NativeHelper_testTemplate(JNIEnv *env, jobject thiz) {
    //simpleLog();
    cout.rdbuf( new AndroidBuf());
    int a = 100;
    func(a);
    int x = 100,y = 9;
    int result = compare(x,y);
    cout << "result = " << result << endl;

    char *s = "hello ni hao !!!";
    string ss = " ni hao a !!!";
    func(&s);
    func(s);
    func(&ss);

}