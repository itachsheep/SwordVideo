//
// Created by wei tao on 2/5/22.
//
#include <jni.h>
#include "x264_encoder.h"

X264Encoder *x264Encoder = 0;

extern "C" JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_H264Manager_enc_1init(JNIEnv *env, jobject thiz,
        jstring h264_path, jint width,jint height,
        jint video_bit_rate,jint frame_rate) {
    if(!x264Encoder) {
        x264Encoder = new X264Encoder();
    }
    const char *h264Path = env->GetStringUTFChars(h264_path,JNI_FALSE);
    x264Encoder->init(h264Path,width,height,video_bit_rate,frame_rate);
    env->ReleaseStringUTFChars(h264_path,h264Path);
}


extern "C" JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_H264Manager_enc_1encode(JNIEnv *env, jobject thiz,
        jbyteArray byte_array,jint yuv_type) {
    jbyte *byteArray = env->GetByteArrayElements(byte_array,JNI_FALSE);
    AVPacket *avPacket = new AVPacket;
    avPacket->data = reinterpret_cast<uint8_t *>(byteArray);
    avPacket->type = yuv_type;
    if(x264Encoder) {
        x264Encoder->encode(avPacket);
    }
    free(avPacket);
    env->ReleaseByteArrayElements(byte_array,byteArray,0);
}

extern "C" JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_H264Manager_enc_1destroy(JNIEnv *env, jobject thiz) {
    if(x264Encoder) {
        x264Encoder->destory();
    }
}
