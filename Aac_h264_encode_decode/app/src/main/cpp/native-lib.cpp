#include <jni.h>
#include <string>
#include "aac_ctrl.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_tao_aac_1h264_1encode_1decode_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C" JNIEXPORT jint JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_initWithADTformat(JNIEnv *env, jobject thiz) {
    return initWithADTSFormat();
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_decode(JNIEnv *env, jobject thiz,
                                                         jbyteArray byte_array, jint len) {
    return nullptr;
}

extern "C" JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_destroy(JNIEnv *env, jobject thiz) {

}