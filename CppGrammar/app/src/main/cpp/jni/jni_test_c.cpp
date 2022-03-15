//
// Created by wei tao on 2022/3/15.
//

#include <jni.h>
#include "my_log.h"

extern "C" {
#include "hello.h"
}
void test() {

}


extern "C"
JNIEXPORT void JNICALL
Java_com_tao_myndktest_NativeHelper_testC(JNIEnv *env, jobject thiz) {
    simpleLog();
    LOGD("call c function from cpp ----->");
    char* res = printHello();

    LOGD("res = %s" , res);

}