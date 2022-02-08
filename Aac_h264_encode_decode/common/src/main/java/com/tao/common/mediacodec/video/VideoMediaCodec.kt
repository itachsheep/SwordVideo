package com.tao.common.mediacodec.video

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import com.tao.common.LogHelper
import com.tao.common.config.VideoConfiguration

object VideoMediaCodec {

    private val TAG = this.javaClass.simpleName

    fun getVideoMediaCodec(videoConfiguration: VideoConfiguration): MediaCodec? {
        var mediaCodec: MediaCodec? = null
        val videoWidth = getVideoSize(videoConfiguration.width)
        val videoHeight = getVideoSize(videoConfiguration.height)
        val format = MediaFormat.createVideoFormat(videoConfiguration.mime, videoWidth, videoHeight)
        if (videoConfiguration.codeType == VideoConfiguration.ICODEC.ENCODE) {
            format.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )
            format.setInteger(MediaFormat.KEY_BIT_RATE, videoConfiguration.maxBps * 1024)
            var fps = videoConfiguration.fps


            //设置摄像头预览帧率
            /*if (BlackListHelper.deviceInFpsBlacklisted()) {
                LogHelper.d(TAG, "Device in fps setting black list, so set mediacodec fps 15")
                fps = 15
            }*/
            format.setInteger(MediaFormat.KEY_FRAME_RATE, fps)
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, videoConfiguration.ifi)
            format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR)
            format.setInteger(MediaFormat.KEY_COMPLEXITY, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR)

            try {
                mediaCodec = MediaCodec.createEncoderByType(videoConfiguration.mime)
                mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                LogHelper.d(TAG, "mediacodec init successed!")
            } catch (e: Exception) {
                e.printStackTrace()
                mediaCodec = release(mediaCodec)
            }
        } else if (videoConfiguration.codeType == VideoConfiguration.ICODEC.DECODE) {
            try {
                mediaCodec = MediaCodec.createDecoderByType(videoConfiguration.mime)
                if (videoConfiguration.surface == null)
                    throw NullPointerException("surface is null?")
                if (videoConfiguration.spspps == null)
                    throw NullPointerException("spspps buffer is null?")

                //csd-0 含义此处有介绍
                // @see https://developer.android.com/reference/android/media/MediaCodec
                format.setByteBuffer("csd-0", videoConfiguration.spspps)
                mediaCodec.configure(format, videoConfiguration.surface, null, 0)
            } catch (e: Exception) {
                e.printStackTrace()
                mediaCodec = release(mediaCodec)
            }
        }
        return mediaCodec
    }

    private fun release(mediaCodec: MediaCodec?): MediaCodec? {
        var mediaCodec1 = mediaCodec
        if (mediaCodec1 != null) {
            mediaCodec1.stop()
            mediaCodec1.release()
            mediaCodec1 = null
        }
        return mediaCodec1
    }

    // We avoid the device-specific limitations on width and height by using values that
    // are multiples of 16, which all tested devices seem to be able to handle.
    fun getVideoSize(size: Int): Int {
        val multiple = Math.ceil(size / 16.0).toInt()
        return multiple * 16
    }


}