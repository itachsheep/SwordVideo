//
// Created by wei tao on 2/21/22.
//
#include <jni.h>
#include "yuv_h264_encode.h"
#include "H264Decoder.h"

H264Encoder *mH264Encoder;
H264Decoder *mH264Decoder;

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
        jobject thiz, jstring in_h264_path,
        jstring out_yuv420sp_path, jint width, jint height,
        jint video_rate) {
    int ret = 0;
    if(mH264Decoder) {
        mH264Decoder->release();
        delete mH264Decoder;
        mH264Decoder = NULL;
    }

    const char *inH264Path = env->GetStringUTFChars(in_h264_path, 0);
    const char *outYUVPath = env->GetStringUTFChars(out_yuv420sp_path, 0);


    mH264Decoder = new H264Decoder();
    ret = mH264Decoder->init(inH264Path,outYUVPath,
            width,height,video_rate);
    env->ReleaseStringUTFChars(in_h264_path,inH264Path);
    env->ReleaseStringUTFChars(out_yuv420sp_path,outYUVPath);
    return ret;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_FFmpegH264Native_start(JNIEnv *env, jobject thiz) {
    if(mH264Encoder) {
        mH264Encoder->start();
    }

    if(mH264Decoder) {
        mH264Decoder->start();
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

    if(mH264Decoder) {
        mH264Decoder->release();
        delete mH264Decoder;
        mH264Decoder = NULL;
    }

}





