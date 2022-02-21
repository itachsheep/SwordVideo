//
// Created by wei tao on 2/21/22.
//
#include <jni.h>

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegH264Native_initEncode(JNIEnv *env,
        jobject thiz, jstring in_yuv420sp_path,
        jstring out_h264_path, jint width, jint height,
        jint fps, jint video_rate) {

    // TODO: implement initEncode()
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegH264Native_initDecode(JNIEnv *env,
        jobject thiz, jstring in_yuv420sp_path,
        jstring out_h264_path, jint width, jint height,
        jint video_rate) {

    // TODO: implement initDecode()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_FFmpegH264Native_start(JNIEnv *env, jobject thiz) {
    // TODO: implement start()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_FFmpegH264Native_release(JNIEnv *env, jobject thiz) {
    // TODO: implement release()
}





