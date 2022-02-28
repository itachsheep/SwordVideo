//
// Created by wei tao on 2/25/22.
//
#include <jni.h>

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_LameMp3Native_init(JNIEnv *env, jobject thiz, jstring out_url, jint sample_rate,
                                       jint channels, jint bit_rate) {
    // TODO: implement init()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_LameMp3Native_encode(JNIEnv *env, jobject thiz, jbyteArray byte_array) {
    // TODO: implement encode()
}



extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_LameMp3Native_release(JNIEnv *env, jobject thiz) {
    // TODO: implement release()
}
