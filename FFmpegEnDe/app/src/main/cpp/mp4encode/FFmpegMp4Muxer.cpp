//
// Created by wei tao on 2022/3/2.
//

#include "FFmpegMp4Muxer.h"

#define IO_BUFFER_SIZE 32768  //缓存32k

static FILE *g_fileH264 = NULL;
static FILE *g_fileAAC = NULL;

template<typename T>
T RandT(T _min, T _max) {
    T temp;
    if (_min > _max) {
        temp = _max;
        _max = _min;
        _min = temp;
    }
    return rand() / (double) RAND_MAX * (_max - _min) + _min;
}

int FillAudioIoBuffer(void *opaque, unsigned char *o_pbBuf, int i_iMaxSize) {
    int iRet = -1;
    if (!feof(g_fileAAC)) {
        iRet = fread(o_pbBuf, 1, RandT(i_iMaxSize / 2, i_iMaxSize), g_fileAAC);
    } else {
    }
    return iRet;
}

/**
 * H264 和 AAC 文件封住 Mp4
 * @param videoPath
 * @param audioPath
 * @param outputPath
 * @return
 */
int FFmpegMp4Muxer::AAC_H264_STREAM_To_MP4(const char *videoPath,
      const char *audioPath, const char *outputPath) {
    simpleLog();
    g_fileAAC = fopen(audioPath, "rb+");
    g_fileH264 = fopen(videoPath, "rb+");

    //1.注册所有组件
    av_register_all();

    //2.打开输出上下文
    avformat_alloc_output_context2(&outFormatCtx, NULL,
                                   "mp4", outputPath);
    if (NULL == outFormatCtx) {
        LOGE("Could't create output context");
        return 0;
    }

    //3.打开音频输入文件上下文
    unsigned char *pbAIoBuf = nullptr;
    AVIOContext * ptAAVIO = nullptr;
    AVInputFormat * ptAInputFormat = nullptr;
    audioFormatCtx = avformat_alloc_context();
    pbAIoBuf = (unsigned char *) av_malloc(IO_BUFFER_SIZE);
    //FillIoBuffer则是将数据读取至pbIoBuf的回调函数。
    // FillIoBuffer()形式（参数，返回值）是固定的，是一个回调函数,
    ptAAVIO = avio_alloc_context(pbAIoBuf, IO_BUFFER_SIZE,
                                 0, NULL,
                                 FillAudioIoBuffer, NULL,
                                 NULL);  //当系统需要数据的时候，
                                 // 会自动调用该回调函数以获取数据

    audioFormatCtx->pb = ptAAVIO; //当自行指定了AVIOContext之后，
    // avformat_open_input()里面的URL参数就不起作用了
    ptAInputFormat = av_find_input_format("aac");//得到ptInputFormat以便后面打开使用
    if (avformat_open_input(&audioFormatCtx, "", ptAInputFormat, 0) < 0) {
        LOGE("Could't open input file");
        return 0;
    }

    if (avformat_find_stream_info(audioFormatCtx, 0) < 0) {
        LOGE("Could't find input stream information");
        return 0;
    }

    //4.打开视频输入文件上下文
    unsigned char *pbVIoBuf = nullptr;
    AVIOContext * ptVAVIO = nullptr;
    AVInputFormat * ptVInputFormat = nullptr;
    videoFormatCtx = avformat_alloc_context();
    pbVIoBuf = (unsigned char *) av_malloc(IO_BUFFER_SIZE);
    //FillIoBuffer则是将数据读取至pbIoBuf的回调函数。FillIoBuffer()形式（参数，返回值）是固定的，是一个回调函数,
    ptVAVIO = avio_alloc_context(pbVIoBuf, IO_BUFFER_SIZE, 0, NULL, FillVideoIoBuffer, NULL,
                                 NULL);  //当系统需要数据的时候，会自动调用该回调函数以获取数据
    videoFormatCtx->pb = ptVAVIO; //当自行指定了AVIOContext之后，avformat_open_input()里面的URL参数就不起作用了
    ptVInputFormat = av_find_input_format("h264");//得到ptInputFormat以便后面打开使用
    if (avformat_open_input(&videoFormatCtx, "", ptVInputFormat, 0) < 0) {
        LOGE("Could't open input file");
        return 0;
    }
    if (avformat_find_stream_info(videoFormatCtx, 0) < 0) {
        LOGE("Could't find input stream information");
        return 0;

    }

}