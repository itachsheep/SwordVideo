//
// Created by wei tao on 2022/1/23.
//

#include "aac_decoder.h"

AACDecoder::AACDecoder() : mSpecInfoSize(64),
                           mDecoder(NULL), mPckSize(-1), mSpecInfo(NULL) {
    simpleLog();
}

AACDecoder::~AACDecoder() {
    simpleLog();
}

int AACDecoder::initWithADTSFormat() {
    simpleLog();
    int nrOfLayers = 1;
    mDecoder = aacDecoder_Open(TT_MP4_ADTS, nrOfLayers);
    printAACInfo();
    return 1;
}

void AACDecoder::printAACInfo() {
    simpleLog();
    CStreamInfo *aac_stream_info = aacDecoder_GetStreamInfo(mDecoder);
    if (aac_stream_info == NULL) {
        LogE("aacDecoder_GetStreamInfo failed");
        return;
    }
    LogD("> stream info: channel = %d\t"
         "sample_rate = %d\t"
         "frame_size = %d\t"
         "aot = %d\t"
         "bitrate = %d\n",
         aac_stream_info->channelConfig,
         aac_stream_info->aacSampleRate,
         aac_stream_info->aacSamplesPerFrame,
         aac_stream_info->aot,
         aac_stream_info->bitRate);
}

int AACDecoder::decode(byte *pck, int len, byte **outBuffer) {
    simpleLog();
    int threshold = (FDK_MAX_AUDIO_FRAME_SIZE * 3) / 2;
    uint8_t pcm_buf[threshold];
    int pcm_buf_index = 0;
    int pcm_buf_size = 0;
    while (len > 0) {
        int data_size = threshold;
        int len1 = this->fdkDecodeAudio((INT_PCM *)(pcm_buf + pcm_buf_index),
                &data_size,pck,len);
        if(len1 < 0) {
            /** if error, skip frame */
            len = 0;
            break;
        }
        pck += len1;
        len -= len1;
        if(data_size <= 0) {
            /** No data yet, get more frames */
            break;
        }
        pcm_buf_index += data_size;
        pcm_buf_size += data_size;
    }
    if(pcm_buf_size > 0) {
        *outBuffer = new byte[pcm_buf_size];
        memcpy(*outBuffer,pcm_buf,pcm_buf_size);
    }

    return pcm_buf_size;
}

/**
 *
 * @param outBuffer
 * @param output_size
 * @param pktBuffer  要解码的数据
 * @param pktSize 要解码的数据大小
 * @return
 */
int AACDecoder::fdkDecodeAudio(INT_PCM *outBuffer,
        int *output_size,
        byte *pktBuffer,
        int pktSize) {
    simpleLog();
    int ret = 0;
    UINT pkt_size = pktSize;
    UINT valid_size = pktSize;
    UCHAR *input_buf[1] = {pktBuffer};

    /** step 1 -> fill aac_data_buf to decoder's internal buf */
    ret = aacDecoder_Fill(mDecoder,input_buf, &pkt_size,&valid_size);
    if(ret != AAC_DEC_OK) {
        LogE("aacDecoder_Fill failed: %x ",ret);
        *output_size = 0;
        return 0;
    }
    int buf_size = mPckSize;
    if(mPckSize <= 0) {
        buf_size = 10 * 1024;
    }

    /** step 2 -> call decoder function */
    int fdk_flags = 0;
    ret = aacDecoder_DecodeFrame(mDecoder,outBuffer,buf_size,fdk_flags);
    if(ret == AAC_DEC_NOT_ENOUGH_BITS) {
        LogE("aacDecoder_DecodeFrame not enough: %x ",ret);
        *output_size = 0;
        return (pktSize - valid_size);
    }

    if(ret != AAC_DEC_OK) {
        LogE("aacDecoder_DecodeFrame failed : %x",ret);
        *output_size = 0;
        return 0;
    }

    if(mPckSize <= 0) {
        initFrameSize();
    }
    *output_size = mPckSize;
    /** return aac decode size */
    return (pktSize - valid_size);

}

void AACDecoder::initFrameSize() {
    simpleLog();
    CStreamInfo *aac_stream_info = aacDecoder_GetStreamInfo(mDecoder);
    mPckSize = aac_stream_info->channelConfig * 2 * aac_stream_info->aacSamplesPerFrame;
    LogD("initFrameSize mPckSize = %d",mPckSize);
}

void AACDecoder::destroy() {
    simpleLog();
    if(mDecoder) {
        aacDecoder_Close(mDecoder);
    }
}


