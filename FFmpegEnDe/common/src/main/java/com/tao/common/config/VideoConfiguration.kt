/**
 * @ClassName:      VideoConfiguration.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 7:46 PM
 */
package com.tao.common.config

import android.media.MediaFormat
import android.view.Surface
import java.nio.ByteBuffer

class VideoConfiguration private constructor(builder:Builder){
    val height: Int
    val width: Int
    val minBps: Int
    val maxBps: Int
    val fps: Int
    val mediaCodec: Boolean
    val ifi: Int
    val codeType: ICODEC
    val mime: String
    var surface: Surface?=null
    var spspps: ByteBuffer?=null

    init {
        height = builder.height
        width = builder.width
        minBps = builder.minBps
        maxBps = builder.maxBps
        fps = builder.fps
        ifi = builder.ifi
        codeType = builder.codeType
        mime = builder.mime
        mediaCodec = builder.mediaCodec
        surface = builder.surface
        spspps = builder.byteBuffer
    }

    class Builder {
        var mediaCodec = DEFAULT_MEDIA_CODEC
        var codeType = DEFAULT_CODEC_TYPE
        var height = DEFAULT_HEIGHT
        var width = DEFAULT_WIDTH
        var minBps = DEFAULT_MIN_BPS
        var maxBps = DEFAULT_MAX_BPS
        var fps = DEFAULT_FPS
        var ifi = DEFAULT_IFI
        var mime = DEFAULT_MIME
        var surface = DEFAULT_DECODE_SURFACE
        var byteBuffer = DEFAULT_SPS_PPS_BUFFER


        fun setSize(width: Int, height: Int): Builder {
            this.width = width
            this.height = height
            return this
        }

        fun setBps(minBps: Int, maxBps: Int): Builder {
            this.minBps = minBps
            this.maxBps = maxBps
            return this
        }

        fun setFps(fps: Int): Builder {
            this.fps = fps
            return this
        }

        fun setIfi(ifi: Int): Builder {
            this.ifi = ifi
            return this
        }

        fun setMime(mime: String): Builder {
            this.mime = mime
            return this
        }

        fun setMediaCodec(meidaCodec: Boolean): Builder {
            this.mediaCodec = meidaCodec
            return this
        }

        fun setCodeType(codeType: ICODEC): Builder {
            this.codeType = codeType
            return this
        }

        fun setSurface(surface: Surface):Builder{
            this.surface = surface;
            return this;
        }

        fun setSpsPpsBuffer(buffer: ByteBuffer):Builder{
            this.byteBuffer = buffer;
            return this;
        }

        fun build(): VideoConfiguration {
            return VideoConfiguration(this)
        }
    }

    companion object {
        val DEFAULT_HEIGHT = 1280
        val DEFAULT_WIDTH = 720
        val DEFAULT_FPS = 25
        val DEFAULT_MAX_BPS = 1800
        val DEFAULT_MIN_BPS = 400
        val DEFAULT_IFI = 2
        val DEFAULT_MIME =  MediaFormat.MIMETYPE_VIDEO_AVC
        val DEFAULT_MEDIA_CODEC = true
        val DEFAULT_CODEC_TYPE = ICODEC.ENCODE
        val DEFAULT_SPS_PPS_BUFFER : ByteBuffer?= null
        /**
         * 用于解码显示的 surface
         */
        val DEFAULT_DECODE_SURFACE: Surface? = null

        fun createDefault(): VideoConfiguration {
            return Builder().build()
        }
    }

    public enum class ICODEC(type: Int) {
        ENCODE(1),
        DECODE(2),
    }
}