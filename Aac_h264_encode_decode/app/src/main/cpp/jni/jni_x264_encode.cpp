//
// Created by wei tao on 2/5/22.
//
#include <jni.h>

extern "C" JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_H264Manager_enc_1init(JNIEnv *env, jobject thiz,
        jstring h264_path, jint width,jint height,
        jint video_bit_rate,jint frame_rate) {
    // TODO: implement enc_init()
}


extern "C" JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_H264Manager_enc_1encode(JNIEnv *env, jobject thiz,
        jbyteArray byte_array,jint yuv_type) {
    // TODO: implement enc_encode()
}

extern "C" JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_H264Manager_enc_1destroy(JNIEnv *env, jobject thiz) {
    // TODO: implement enc_destroy()
}
