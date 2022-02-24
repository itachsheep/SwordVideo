//
// Created by wei tao on 2/24/22.
//

#ifndef FFMPEGENDE_H264DECODER_H
#define FFMPEGENDE_H264DECODER_H

#include "my_log.h"
#include "pthread.h"

extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/time.h"
};

class H264Decoder {
private:
    /**
     * 封装格式上下文
     */
    AVFormatContext *pFormatCtx = 0;
    /**
     * 编解码器上下文
     */
    AVCodecContext *pCodecCtx = 0;
    /**
     * 视频宽高码率
     */
    int mWidth = 0, mHeight = 0, mVideoRate = 0;

    /**
     * 解码线程 ID
     */
    pthread_t mTID;
    /**
     * 是否开始解码
     */
    int isStart = 0;

    /**
     * 输出文件路径
     */
    FILE *pOutFile = 0;

private:
    int allo_video_stream_info();

public:
    /**
     * 初始化
     */
    int init(const char *inH264Path, const char *outYUVPath, int w, int h, int videoRate);

    /**
     * 解码
     * @param avPacket  AAC 数据包
     * @return
     */
    int decode(AVPacket *avPacket);

    /**
     * 释放
     */
    void release();

    /**
     * 开启解码线程
     */
    void start();

    /**
     * 内部是子线程在解码
     */
    void startDecoder();
};


#endif //FFMPEGENDE_H264DECODER_H
