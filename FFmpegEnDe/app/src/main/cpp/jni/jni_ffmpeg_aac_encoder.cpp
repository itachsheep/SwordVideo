//
// Created by wei tao on 2/12/22.
//
#include <jni.h>
#include "audio_encoder.h"

AudioEncoder *mAudioEncoder;


void EncodeCallback(uint8_t *data, int len){

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_init__Ljava_lang_String_2III(
        JNIEnv *env, jobject thiz,jstring out_aacpath, jint bit_rate,
        jint channels, jint sample_rate) {
    simpleLog();
    if(mAudioEncoder) {
        mAudioEncoder->release();
        mAudioEncoder = NULL;
    }
    int ret = 0;
    if(mAudioEncoder == NULL) {
        mAudioEncoder = new AudioEncoder();
    }
    const char* outPath = env->GetStringUTFChars(out_aacpath,0);
    ret = mAudioEncoder->init(outPath,bit_rate,
            channels,sample_rate,"libfdk_aac");
    mAudioEncoder->addEncodeCallback(EncodeCallback);
    env->ReleaseStringUTFChars(out_aacpath,outPath);
    return ret;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_encode(
        JNIEnv *env, jobject thiz, jbyteArray byte_array) {
    int ret = 0;
    if(mAudioEncoder == NULL) {
        return 0;
    }
    jbyte *jbyte1 = env->GetByteArrayElements(byte_array,0);
    PCMPacket *pcmPacket = static_cast<PCMPacket *>(malloc(sizeof(PCMPacket)));
    pcmPacket->len = env->GetArrayLength(byte_array);
    pcmPacket->data = reinterpret_cast<uint8_t *>(jbyte1);
    ret = mAudioEncoder->encode(pcmPacket);
    free(pcmPacket);
    env->ReleaseByteArrayElements(byte_array,jbyte1,0);
    return ret;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_FFmpegAacNative_release(JNIEnv *env, jobject thiz) {
    simpleLog();
    if (mAudioEncoder) {
        mAudioEncoder->release();
        mAudioEncoder = NULL;
    }
}