//
// Created by wei tao on 2022/3/2.
//
#include <jni.h>
#include "FFmpegMp4Muxer.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_tao_ffmpeg_NativeMuxer_aac_1h264_1muxerToMp4(JNIEnv *env,
        jobject thiz,jstring h264_path,
        jstring audio_path, jstring output_path) {
    simpleLog();

    const char *videoPath = env->GetStringUTFChars(h264_path, NULL);
    const char *audioPath = env->GetStringUTFChars(audio_path, NULL);
    const char *outputPath = env->GetStringUTFChars(output_path, NULL);

    FFmpegMp4Muxer *fFmpegMuxer = new FFmpegMp4Muxer();

//    fFmpegMuxer->AAC_H264_FILE_To_MP4(videoPath, audioPath, outputPath);
    fFmpegMuxer->AAC_H264_STREAM_To_MP4(videoPath, audioPath, outputPath);
//    fFmpegMuxer->H264_stream_To_MP4(videoPath, audioPath, outputPath);

    delete fFmpegMuxer;
    env->ReleaseStringUTFChars(h264_path, videoPath);
    env->ReleaseStringUTFChars(audio_path, audioPath);
    env->ReleaseStringUTFChars(output_path, outputPath);

}