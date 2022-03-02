//
// Created by wei tao on 2022/3/2.
//

#ifndef FFMPEGENDE_FFMPEGMP4MUXER_H
#define FFMPEGENDE_FFMPEGMP4MUXER_H

#include "my_log.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
#include <libswscale/swscale.h>
};


class FFmpegMp4Muxer {
private:
    int frameIndex = 0;

    int videoIndex = -1;
    int audioIndex = -1;

    int audioStreamIndex = -1;
    int videoStreamIndex = -1;

    int64_t cur_pts_v = 0, cur_pts_a = 0;

    AVPacket avPacket;
    AVFormatContext *videoFormatCtx = NULL;
    AVFormatContext *audioFormatCtx = NULL;

public:
    AVFormatContext *outFormatCtx = NULL;

    int NewStream(AVFormatContext *avFormatContext, int &inputIndex, AVMediaType mediaType);

    void WritePTS(AVPacket *avPacket, AVStream *inputStream);

    /**
     * AAC H264 open_input 方式读取文件
     * @param videoPath
     * @param audioPath
     * @param outputPath
     * @return
     */
//    int AAC_H264_FILE_To_MP4(const char *videoPath, const char *audioPath, const char *outputPath);

    /**
     * FFMpeg 直接读取视频 H264 裸流数据
     * @param videoPath
     * @param audioPath
     * @param outputPath
     * @return
     */
//    int H264_2_MP4(const char *videoPath, const char *audioPath, const char *outputPath);


    /**
   * FFMpeg 直接读取 AAC 裸流数据
   * @param videoPath
   * @param audioPath
   * @param outputPath
   * @return
   */
//    int AAC_2_MP4(const char *videoPath, const char *audioPath, const char *outputPath);


    /**
    * FFMpeg 读取 AAC H264 裸流数据
    * @param videoPath
    * @param audioPath
    * @param outputPath
    * @return
    */
    int AAC_H264_STREAM_To_MP4(const char *videoPath, const char *audioPath, const char *outputPath);

};


#endif //FFMPEGENDE_FFMPEGMP4MUXER_H
