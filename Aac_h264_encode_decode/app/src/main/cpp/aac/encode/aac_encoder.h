//
// Created by wei tao on 2/4/22.
//

#ifndef AAC_H264_ENCODE_DECODE_AAC_ENCODER_H
#define AAC_H264_ENCODE_DECODE_AAC_ENCODER_H

#include <aacenc_lib.h>
#include <zconf.h>
#include <stdio.h>

typedef enum {
    LC_AAC = 2,
    HE_AAC = 5,
    LC_v2_AAC = 29,
} AACProfile;

class AACEncoder {
private:
    HANDLE_AACENCODER mAacencoder;
    AACENC_InfoStruct mEncInfo = {0};
    uint8_t *mInBuffer = 0;
    int mInBufferCursor;
    int mInputSizeFixed;
    uint8_t mAacOutbuf[20480];
    /**
     * 设置编码 Header
     */
    bool isFlagGlobalHeader = false;
    FILE *aacFile = 0;
public:
    AACEncoder();
    ~AACEncoder();
    int init(AACProfile profile,
            int sampleRate, int channels, int bitRate);
    int encode(Byte *pData, int dataByteSize, char **outBuffer);
    void destory();
    int fdkEncodeAudio();
    void addADTS2Packet(uint8_t *packet,int packetLen);
    void writeAACPacketToFile(uint8_t *string, int i);
};


#endif //AAC_H264_ENCODE_DECODE_AAC_ENCODER_H
