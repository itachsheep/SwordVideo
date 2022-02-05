//
// Created by wei tao on 2/4/22.
//
#include <jni.h>
#include "aac_encoder.h"

/************************************* encode ************************************/

AACEncoder *mAACEncode = 0;

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_enc_1init(
        JNIEnv *env, jobject thiz, jint bit_rate, jint channel,jint sample_rate) {
    simpleLog();
    if(!mAACEncode) {
        mAACEncode = new AACEncoder();
    }
    AACProfile profile = LC_AAC;
    mAACEncode->init(profile,sample_rate,channel,bit_rate);
    return 1;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_encode(JNIEnv *env, jobject thiz,jbyteArray byte_array, jint buffer_size) {
    jbyte *pcm = env->GetByteArrayElements(byte_array,0);
    //simpleLog();
    int ret = 0;
    char *outBuffer = 0;
    if(mAACEncode) {
        ret = mAACEncode->encode((Byte *)pcm,buffer_size,&outBuffer);
    }
    env->ReleaseByteArrayElements(byte_array,pcm,0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_enc_1destroy(JNIEnv *env, jobject thiz) {
    simpleLog();
    if(mAACEncode) {
        mAACEncode->destory();
        delete mAACEncode;
        mAACEncode = 0;
    }
}