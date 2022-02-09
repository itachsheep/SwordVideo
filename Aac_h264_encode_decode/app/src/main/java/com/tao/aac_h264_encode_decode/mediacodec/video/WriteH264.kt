/**
 * @ClassName:      WriteH264.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 8:08 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec.video

import android.content.Context
import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import com.tao.common.LogHelper
import com.tao.common.callback.OnVideoEncodeListener
import com.tao.common.config.VideoConfiguration
import com.tao.common.mediacodec.video.H264Encoder
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.experimental.and

class WriteH264(val outputPath: String):H264Encoder() {

    private var mRenderer: DevYKSurfaceRenderer? = null
    private var mContext: Context? = null

    private var mFileOutputStream: FileOutputStream? = null

    private var listener: OnVideoEncodeListener? = null


    override fun onSurfaceCreate(surface: Surface?) {
        super.onSurfaceCreate(surface)
        /**
         * 使用mediaCodec 里面的surface
         * mSurface = mMediaCodec!!.createInputSurface()
         * 将绘制内容绘制到 mediaCodec 的 surface中
         */
        mRenderer = DevYKSurfaceRenderer(mContext!!, surface!!)
        mRenderer?.start()
    }

    override fun onSurfaceDestory(surface: Surface?) {
        super.onSurfaceDestory(surface)
        mRenderer?.stopAndWait()
    }

    public fun setOnEncodeListener(listener: OnVideoEncodeListener) {
        this.listener = listener
    }

    /**
     * 准备编码
     */
    fun prepare(context: Context, convideoConfiguration: VideoConfiguration) {
        mContext = context
        prepare(convideoConfiguration)


    }

    /**
     * 开始编码
     */
    override fun start() {
        super.start()
//        mFileOutputStream = FileOutputStream("sdcard/avsample/mediacodec_video.h264")
        mFileOutputStream = FileOutputStream(outputPath)
    }

    /**
     * 停止编码
     */
    override fun stop() {
        super.stop()
        mFileOutputStream?.close()
    }

    /**
     * 编码完成的 H264 数据
     *   00 00 00 01 06:  SEI信息
     *   00 00 00 01 67:  0x67&0x1f = 0x07 :SPS
     *   00 00 00 01 68:  0x68&0x1f = 0x08 :PPS
     *   00 00 00 01 65:  0x65&0x1f = 0x05: IDR Slice
     */
    override fun onVideoEncode(bb: ByteBuffer?, bi: MediaCodec.BufferInfo) {
        Log.e(TAG, bi.size.toString())
        val h264Arrays = ByteArray(bi.size)
        bb?.position(bi.offset)
        bb?.limit(bi.offset + bi.size)
        bb?.get(h264Arrays)
        val tag = h264Arrays[4].and(0x1f).toInt()
        if (tag == 0x07) {//sps
            LogHelper.e(TAG, " SPS " + h264Arrays.size)
        } else if (tag == 0x08) {//pps
            LogHelper.e(TAG, " PPS ")
        } else if (tag == 0x05) {//关键字帧
            LogHelper.e(TAG, " 关键帧 " + h264Arrays.size)
        } else {
            //普通帧
            //LogHelper.e(TAG, " 普通帧 " + h264Arrays.size)
        }
        listener?.onVideoEncode(bb!!,bi)
        mFileOutputStream?.write(h264Arrays)
    }


    override fun onVideoOutformat(outputFormat: MediaFormat?) {

    }
}