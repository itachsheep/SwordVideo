//
// Created by wei tao on 2/21/22.
//
#include <jni.h>
#include "yuv_h264_encode.h"

H264Encoder *mH264Encoder;

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegH264Native_initEncode(JNIEnv *env,
        jobject thiz, jstring in_yuv420sp_path,
        jstring out_h264_path, jint width, jint height,
        jint fps, jint video_rate) {

    int ret = 0;
    const char *inYuvPath = env->GetStringUTFChars(in_yuv420sp_path, 0);
    const char *outH264Path = env->GetStringUTFChars(out_h264_path, 0);

    if(mH264Encoder) {
        mH264Encoder->release();
        delete mH264Encoder;
        mH264Encoder = NULL;
    }

    mH264Encoder = new H264Encoder();
    ret = mH264Encoder->init(inYuvPath,outH264Path,width,height,fps,video_rate);
    env->ReleaseStringUTFChars(in_yuv420sp_path, inYuvPath);
    env->ReleaseStringUTFChars(out_h264_path, outH264Path);
    LogD("_initEncode ret = %d",ret);
    return ret;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegH264Native_initDecode(JNIEnv *env,
        jobject thiz, jstring in_yuv420sp_path,
        jstring out_h264_path, jint width, jint height,
        jint video_rate) {

    simpleLog();
    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_FFmpegH264Native_start(JNIEnv *env, jobject thiz) {
    if(mH264Encoder) {
        mH264Encoder->start();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_FFmpegH264Native_release(JNIEnv *env, jobject thiz) {
    if(mH264Encoder) {
        mH264Encoder->release();
        delete mH264Encoder;
        mH264Encoder = NULL;
    }
}





