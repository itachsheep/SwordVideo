//
// Created by wei tao on 2/18/22.
//
#include <jni.h>
#include "audio_decoder.h"

#define FDK_AAC "libfdk_aac"
#define X264 "libx264"

AudioDecoder *mAudioDecoder;
extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_init__Ljava_lang_String_2Ljava_lang_String_2(
        JNIEnv *env,jobject thiz,
        jstring in_aacpath,jstring out_pcmpath) {
    if(mAudioDecoder) {
        mAudioDecoder->release();
        mAudioDecoder = NULL;
    }
    int ret = 0;
    if(mAudioDecoder == NULL) {
        mAudioDecoder = new AudioDecoder();
    }
    const char *in_url = env->GetStringUTFChars(in_aacpath, 0);
    const char *out_url = env->GetStringUTFChars(out_pcmpath, 0);

    ret = mAudioDecoder->init(in_url,out_url,FDK_AAC);
    mAudioDecoder->start();
    env->ReleaseStringUTFChars(in_aacpath,in_url);
    env->ReleaseStringUTFChars(out_pcmpath,out_url);

    return ret;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_releaseDecoder(JNIEnv *env, jobject thiz) {
    if(mAudioDecoder) {
        mAudioDecoder->release();
        delete mAudioDecoder;
        mAudioDecoder = NULL;
    }
}

void *test_thread(void *pVoid){
    int num = 1;
    while (num < 20){
        LogD("test_thread %d",num);
        av_usleep(1000 * 10);
        num++;
    }
    return 0;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_test(JNIEnv *env, jobject thiz) {
    pthread_t id;
    char *s = "111";
    pthread_create(&id,NULL,test_thread,s);
}


