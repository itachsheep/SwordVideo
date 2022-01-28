//
// Created by wei tao on 2022/1/23.
//

#include "aac_decoder.h"

AACDecoder::AACDecoder() : mSpecInfoSize(64),
                           mDecoder(NULL), mPckSize(-1), mSpecInfo(NULL) {

}

AACDecoder::~AACDecoder() {

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

    return 0;
}

void AACDecoder::destroy() {

}


