
# 一个小白剑指音视频之路

## 阶段一：

- [1]

https://blog.csdn.net/leixiaohua1020/article/details/50535042  视音频数据处理入门-雷霄骅

视音频数据处理入门：RGB、YUV像素数据处理
视音频数据处理入门：PCM音频采样数据处理
视音频数据处理入门：H.264视频码流解析
视音频数据处理入门：AAC音频码流解析
视音频数据处理入门：FLV封装格式解析
视音频数据处理入门：UDP-RTP协议解析

> 代码：SwordVideo/CLionProjects/video_basic_lrn


- [2]

https://juejin.cn/post/6844904061771186189  音视频学习 (七) 掌握音频基础知识并使用 AudioTrack、OpenSL ES 渲染 PCM 数据

音频采样
音频编码

https://juejin.cn/post/6844904064401178632  掌握视频基础知识并使用 OpenGL ES 2.0 渲染 YUV 数据
https://blog.csdn.net/guyuealian/article/details/82021607 .  opengl ndk 绘制三角形

> 代码：SwordVideo/Pcm_YUV_Opensl_Opengl_basic_lrn


## 阶段二：

现在可以入手音视频 AAC 、H264 软硬编解码了

https://juejin.cn/user/3368559355637566/posts   DevYk博客
https://github.com/yangkun19921001/AVSample  DevYk github (yangkun19921001/AVSample)

1，aac 编解码

`使用lib_aac.a `
` pcm -> aac `
` aac -> pcm`

> 代码：SwordVideo/Aac_h264_encode_decode`


2，h264 编码

`使用lib_x264.a `
` yuv -> h264`
` h264 -> yuv`

> 代码：SwordVideo/Aac_h264_encode_decode`


3，MediaCodec 实现 AAC 硬编解码

https://www.jianshu.com/p/14daab91b951  (Android音视频之使用MediaCodec编解码AAC)

`使用mediaCodec完成 `
`aac -> pcm`
`pcm -> aac`

> 代码：SwordVideo/Aac_h264_encode_decode`

4，MediaCodec 实现 h264 硬编解码

`使用mediaCodec，通过canvas绘制在surface上的帧画面，编码成x264`

> 代码：SwordVideo/Aac_h264_encode_decode`


5，FFmpeg API 实现音频 AAC 软编解码

https://blog.csdn.net/leixiaohua1020/article/details/25430449  (雷霄骅 --- 最简单的基于FFMPEG的音频编码器（PCM编码为AAC）)

`aac -> pcm`
`pcm -> aac`

> 代码：SwordVideo/FFmpegEnDe`

6, FFmpeg API 实现视频 H264 软编解码

https://blog.csdn.net/leixiaohua1020/article/details/25430425  lei 雷霄骅 ---最简单的基于FFMPEG的视频编码器（YUV编码为H.264）


ffmpeg 实现  

`yuv -> h264`
`h264 -> yuv`


