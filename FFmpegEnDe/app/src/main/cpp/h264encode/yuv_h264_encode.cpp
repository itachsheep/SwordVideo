//
// Created by wei tao on 2/21/22.
//

#include "yuv_h264_encode.h"

int H264Encoder::init(const char *inYUV420sp,
        const char *outH264, int width, int height,
        int fps, int videoRitRate) {
    this->mHeight = height;
    this->mWidht = width;
    this->fps = fps;
    this->mY_size = width * height;
    this->mUV_size = mY_size / 4;
    this->mVideoBitRate = videoRitRate;

    //注册函数的返回值
    int ret = 0;
    //注册 FFmpeg 所有函数
    av_register_all();
    //初始化封装格式上下文
    this->pFormatCtx = avformat_alloc_context();
    //拿到输出的上下文
    this->pOFmt = av_guess_format(NULL, outH264, NULL);
    this->pFormatCtx->oformat = this->pOFmt;

    //打开输出文件
    if (avio_open(&this->pFormatCtx->pb, outH264, AVIO_FLAG_READ_WRITE) < 0) {
        LOGE("open file error:%s\n", outH264);
        release();
        return ret = 0;
    }

    //初始化一个新的视频流，和配置一些编码参数，最后打开编码器
    ret = alloc_video_stream();
    if (!ret) {
        LOGE("alloc_video_stream failed, ret = %d",ret);
        return 0;
    }

    av_dump_format(this->pFormatCtx, 0, outH264, 1);
    ret = alloc_video_frame();
    if (!ret) {
        LOGE("alloc_video_frame failed, ret = %d",ret);
        return 0;
    }

    ret = avformat_write_header(this->pFormatCtx, NULL);
    this->pReadYUV = fopen(inYUV420sp, "rb");
    this->pWriteH264 = fopen(outH264, "wb");
    if (!pReadYUV || !pWriteH264) {
        LOGE("check the input:[%s] and output:[%s] path is correct?\n", inYUV420sp, outH264);
        return 0;
    }
    return ret == AVSTREAM_INIT_IN_WRITE_HEADER;
}

