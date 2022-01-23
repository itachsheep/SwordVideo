#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_tao_aac_1h264_1encode_1decode_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C" JNIEXPORT jint JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_initWithADTformat(JNIEnv *env, jobject thiz) {
    // TODO: implement initWithADTformat()
}

extern "C" JNIEXPORT jint JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_initWithRAWformat(JNIEnv *env, jobject thiz,
                                                                    jbyteArray spec_info,
                                                                    jbyteArray size) {
    // TODO: implement initWithRAWformat()
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_decode(JNIEnv *env, jobject thiz,
                                                         jbyteArray byte_array, jint len) {
    // TODO: implement decode()
}

extern "C" JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_AacManager_destroy(JNIEnv *env, jobject thiz) {
    // TODO: implement destroy()
}