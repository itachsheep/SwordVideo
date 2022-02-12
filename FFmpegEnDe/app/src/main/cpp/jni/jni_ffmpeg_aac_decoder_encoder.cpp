//
// Created by wei tao on 2/12/22.
//
#include <jni.h>

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_init__Ljava_lang_String_2III(JNIEnv *env, jobject thiz,
                                                                 jstring out_aacpath, jint bit_rate,
                                                                 jint channels, jint sample_rate) {

}


extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_init__Ljava_lang_String_2Ljava_lang_String_2(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jstring in_aacpath,
                                                                                 jstring out_pcmpath) {

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_encode(JNIEnv *env, jobject thiz, jbyteArray byte_array) {

}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_release(JNIEnv *env, jobject thiz) {

}