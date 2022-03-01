//
// Created by wei tao on 2/25/22.
//
#include <jni.h>
#include "LameEncoder.h"


LameEncoder *lameEncoder = 0;

extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_ffmpeg_LameMp3Native_init(JNIEnv *env, jobject thiz,
                                       jstring out_url, jint sample_rate,
                                       jint channels, jint bit_rate) {
    simpleLog();
    if (lameEncoder) {
        lameEncoder->release();
        delete lameEncoder;
        lameEncoder = 0;
    }
    int ret = 0;
    const char *outPath = env->GetStringUTFChars(out_url, 0);
    lameEncoder = new LameEncoder();
    ret = lameEncoder->init(outPath, sample_rate, channels, bit_rate);
    env->ReleaseStringUTFChars(out_url, outPath);
    return ret;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_LameMp3Native_encode(JNIEnv *env, jobject thiz,
                                         jbyteArray byte_array) {
    int ret = 0;
    jbyte *pcmArray = env->GetByteArrayElements(byte_array, JNI_FALSE);
    if (lameEncoder) {
        ret = lameEncoder->encode((uint8_t *) pcmArray,
                                  env->GetArrayLength(byte_array));
    }
    env->ReleaseByteArrayElements(byte_array, pcmArray, 0);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_LameMp3Native_release(JNIEnv *env, jobject thiz) {
    simpleLog();
    if (lameEncoder) {
        lameEncoder->release();
        delete lameEncoder;
        lameEncoder = 0;
    }
}
