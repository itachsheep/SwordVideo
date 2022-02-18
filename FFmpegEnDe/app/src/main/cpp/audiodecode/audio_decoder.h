//
// Created by wei tao on 2/18/22.
//

#ifndef FFMPEGENDE_AUDIO_DECODER_H
#define FFMPEGENDE_AUDIO_DECODER_H

#define PUBLISH_BITE_RATE 64000

#include "my_log.h"
#include <pthread.h>

extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/time.h"
};

typedef struct AACPacket {
    AVPacket *packet = 0;
};

class AudioDecoder {
private:
    /**
     * 封装格式上下文
     */
    AVFormatContext *pFormatCtx = 0;
    /**
     * 编码器上下文
     */
    AVCodecContext *pCodeccCtx = 0;
    /**
     * 采样率
     */
    int mSampleRate = 0;
    /**
     * 码率
     */
    int mBitRate = 0;
    /**
     * 通道
     */
    int mChannels = 0;

    /**
     * 是否开始
     */
    int isStart = 0;

    pthread_t id;

private:
    /**
     * 实例化一个音频流
     * @param code_name
     * @return
     */
    int alloc_audio_stream(const char *code_name);

    /**
     * 将  PCM 写入文件
     */
    void writePCMPakcetToFile(uint8_t *data, int datalen);

public:
    int init(const char *inAACPath, const char *outPCMPath,
             const char *codec_name);

    int decode(AACPacket *packet);

    void release();


    void start();

    void startDecoder();
};


#endif //FFMPEGENDE_AUDIO_DECODER_H
