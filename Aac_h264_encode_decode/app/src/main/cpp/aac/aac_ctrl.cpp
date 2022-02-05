//
// Created by wei tao on 2022/1/24.
//

#include "aac_ctrl.h"
#include "decode/aac_decoder.h"

AACDecoder *aacDecoder = 0;

int initWithADTSFormat() {
    simpleLog();
    int ret = 0;
    if(!aacDecoder) {
        aacDecoder = new AACDecoder();
    }
    ret = aacDecoder->initWithADTSFormat();
    return ret;
}

jbyteArray decode(JNIEnv *jniEnv, jobject jobje, jbyteArray aacByte, jint byteSize) {
    //todo:
    // 1，nullptr 和 NULL区别
    // 2，GetByteArrayElements 什么？？
    // 3，reinterpret_cast ？？
    // 4，ReleaseByteArrayElements ？？
    int ret = 0;
    ////java中的byte[]最终在JNI中被转化为jbyteArray，但是jbyteArray要想在C语言中使用，
    /// 还必须得到一个C语言中可识别的char类型指针的形式，
    /// 这就是函数GetByteArrayElements的作用
    jbyte *aac = jniEnv->GetByteArrayElements(aacByte,0);
    byte *outFrame = nullptr;
    jbyteArray pcmByte = nullptr;
    if(aacDecoder) {
        ret = aacDecoder->decode((byte *)aac,byteSize,&outFrame);
        if(outFrame) {
            pcmByte = jniEnv->NewByteArray(ret);
            jniEnv->SetByteArrayRegion(pcmByte,0,ret, reinterpret_cast<jbyte *>(outFrame));
            delete [] outFrame;
        }
    }
    jniEnv->ReleaseByteArrayElements(aacByte,aac,0);
    return pcmByte;
}

void destroy() {
    if(aacDecoder) {
        aacDecoder->destroy();
        delete aacDecoder;
        aacDecoder = 0;
    }
}
