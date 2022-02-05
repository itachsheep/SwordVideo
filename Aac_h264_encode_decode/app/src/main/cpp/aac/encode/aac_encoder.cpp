//
// Created by wei tao on 2/4/22.
//

#include "aac_encoder.h"

AACEncoder::AACEncoder() {

}

AACEncoder::~AACEncoder() {

}

static const char* fdkaac_error(AACENC_ERROR erraac) {
    switch (erraac) {
        case AACENC_OK:
            return "No error";
        case AACENC_INVALID_HANDLE:
            return "Invalid handle";
        case AACENC_MEMORY_ERROR:
            return "Memory allocation error";
        case AACENC_UNSUPPORTED_PARAMETER:
            return "Unsupported parameter";
        case AACENC_INVALID_CONFIG:
            return "Invalid config";
        case AACENC_INIT_ERROR:
            return "Initialization error";
        case AACENC_INIT_AAC_ERROR:
            return "AAC library initialization error";
        case AACENC_INIT_SBR_ERROR:
            return "SBR library initialization error";
        case AACENC_INIT_TP_ERROR:
            return "Transport library initialization error";
        case AACENC_INIT_META_ERROR:
            return "Metadata library initialization error";
        case AACENC_ENCODE_ERROR:
            return "Encoding error";
        case AACENC_ENCODE_EOF:
            return "End of file";
        default:
            return "Unknown error";
    }
}

int AACEncoder::init(AACProfile profile,
        int sampleRate, int channels, int bitRate) {
    simpleLog();
    int result = 0;
    AACENC_ERROR ret;
    // 打开编码器,如果非常需要节省内存则可以调整encModules
    if((ret = aacEncOpen(&mAacencoder,0x0,channels)) != AACENC_OK) {
        LogE("Unable to open fdkaac encoder, ret = 0x%x, error is %s\n",
                ret, fdkaac_error(ret));
        return result;
    }
    /**编码规格
     *  - 2: LC. MPEG-4 AAC Low Complexity
     *  - 5: HE-AAC. MPEG-4 AAC Low Complexity with Spectral Band Replication
     *  - 29: HE-AAC v2. AAC Low Complexity with Spectral Band Replication and Parametric Stereo(声道数必须是2)
     */
    // 设置AAC标准格式
    if ((ret = aacEncoder_SetParam(mAacencoder, AACENC_AOT, profile))
        != AACENC_OK) /* aac lc */{
        LogE("Unable to set the AACENC_AOT, ret = 0x%x, error is %s\n",
                ret, fdkaac_error(ret));
        destory();
        return false;
    }

    // 设置采样率
    if ((ret = aacEncoder_SetParam(mAacencoder, AACENC_SAMPLERATE, sampleRate))
        != AACENC_OK) {
        LogE("Unable to set the SAMPLERATE, ret = 0x%x, error is %s\n",
                ret, fdkaac_error(ret));
        destory();
        return false;
    }

    // 设置通道数
    if ((ret = aacEncoder_SetParam(mAacencoder, AACENC_CHANNELMODE, channels))
        != AACENC_OK) {
        LogE("Unable to set the AACENC_CHANNELMODE, ret = 0x%x, error is %s\n",
                ret, fdkaac_error(ret));
        destory();
        return false;
    }

    // 设置比特率
    if ((ret = aacEncoder_SetParam(mAacencoder, AACENC_BITRATE, bitRate))
        != AACENC_OK) {
        LogE("Unable to set the AACENC_BITRATE, ret = 0x%x, error is %s\n",
                ret, fdkaac_error(ret));
        destory();
        return false;
    }

    /**
     *  设置输出格式，是AAC的裸流还是ADTS的流
     *  0-raw 2-adts
     */
    int encode_mode = TT_MP4_ADTS; //已经封装好了 ADTS 流
    //     encode_mode = TT_MP4_LATM_MCP1; //LATM 流
    if (isFlagGlobalHeader) {
        encode_mode = TT_MP4_RAW;//没有任何格式的封装
    }

    // 设置编码出来的数据带aac adts头
    if ((ret = aacEncoder_SetParam(mAacencoder, AACENC_TRANSMUX, encode_mode))
        != AACENC_OK) /*  0-raw 2-adts */{
        LogE("Unable to set the ADTS AACENC_TRANSMUX, ret = 0x%x, error is %s\n",
                ret, fdkaac_error(ret));
        destory();
        return false;
    }

    // 初始化编码器
    if ((ret = aacEncEncode(mAacencoder, NULL, NULL, NULL, NULL))
        != AACENC_OK) {
        LogE("Unable to initialize the aacEncEncode, ret = 0x%x, error is %s\n",
                ret, fdkaac_error(ret));
        destory();
        return false;
    }

    // 获取编码器信息
    if ((ret = aacEncInfo(mAacencoder, &mEncInfo)) != AACENC_OK) {
        LogE("Unable to get the aacEncInfo info, ret = 0x%x, error is %s\n",
                ret, fdkaac_error(ret));
        destory();
        return false;
    }

    // 计算pcm帧长
    mInputSizeFixed = channels * 2 * mEncInfo.frameLength; //frameLength是每帧每个channel的采样点数
    mInBufferCursor = 0;
    mInBuffer = new uint8_t[mInputSizeFixed];
    memset(mInBuffer, 0, mInputSizeFixed);
    LogD("init 固定每次编码长度为 pcm帧长 = %d",mInputSizeFixed);

    if (0 != access(aacDir, 0)) {
        //TODO -- 没有找到 NDK 中怎么创建文件
//        mkdir("/sdcard/avsample/", 777);
        LogE("not find Dir: /storage/emulated/0/Android/data/com.tao.aac_h264_encode_decode/files/");
        return 0;
    }

    char *filePath = strcat(aacDir,"my_record_tmp.aac");
    aacFile = fopen(filePath,"wb+");
    LogD("init aac编码文件输出路径  = ", filePath);
    result = 1;
    return result;
}

/**
 *
 * @param pData  需要编码的数据
 * @param dataByteSize 编码数据的长度
 * @param outBuffer 输出的 AAC
 * @return 输出 AAC 的长度
 */
int AACEncoder::encode(Byte *pData, int dataByteSize, char **outBuffer) {
    int ret = 0;
    return ret;
}

void AACEncoder::destory() {

}


