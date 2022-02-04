//
// Created by wei tao on 2/4/22.
//
#include <jni.h>

/************************************* encode ************************************/
extern "C"
JNIEXPORT jint JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_enc_1init(JNIEnv *env, jobject thiz,
                                                            jint bit_rate, jint channel,
                                                            jint sample_rate) {

}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_encode(JNIEnv *env, jobject thiz,
                                                         jbyteArray byte_array, jint buffer_size) {
    // TODO: implement encode()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_enc_1destroy(JNIEnv *env, jobject thiz) {
    // TODO: implement enc_destroy()
}