/**
 * @ClassName:      BaseVideoDecoder.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 7:44 PM
 */
package com.tao.common.mediacodec.video

import android.media.MediaCodec
import android.os.Build
import com.tao.common.LogHelper
import com.tao.common.config.VideoConfiguration
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock

open class BaseVideoDecoder: IVideoCodec {
    public val TAG = this.javaClass.simpleName

    private var mWorker: Worker? = null


    /**
     * 解码的准备工作，需要配置 spspps mediacodec等一些信息
     */
    public open fun configure(videoConfiguration: VideoConfiguration) {
        super.prepare(videoConfiguration)
        videoConfiguration?.run {
            mWorker?.configure(this)
        }
    }

    /**
     * 开始解码
     */
    override fun start() {
        mWorker = Worker(this);
        mWorker?.setRunning(true)
        mWorker?.start()
    }

    /**
     * 停止解码
     */
    override fun stop() {
        mWorker?.setRunning(false)
        mWorker = null
    }

    /**
     * 解码完成的数据
     */
    override fun onVideoEncode(bb: ByteBuffer?, mBufferInfo: MediaCodec.BufferInfo) {

    }

    /**
     * 待解码数据送入解码器
     */
    public fun enqueue(byteArray: ByteArray,timeoutUs: Long, flag: Int) {
        mWorker?.enqueue(byteArray, timeoutUs,flag)
    }

    private class Worker(decoder: BaseVideoDecoder) : Thread() {
        private val decodeLock = ReentrantLock()
        @Volatile
        private var isStarted: Boolean = false
        private var mMediaCodec: MediaCodec? = null
        @Volatile
        private var mConfigured: Boolean = false
        private var mConfiguration: VideoConfiguration? = null
        private var baseVideoEncoder: BaseVideoDecoder? = null

        private var TAG = this.javaClass.simpleName

        private var  mTimeoutUs = 10000L
        init {
            baseVideoEncoder = decoder
        }

        public fun configure(videoConfiguration: VideoConfiguration?) {
            videoConfiguration?.run {
                mConfiguration = this
                mMediaCodec = VideoMediaCodec.getVideoMediaCodec(this)
                mMediaCodec?.start()
                mConfigured = true
            }
        }

        fun setRunning(isRuning: Boolean) {
            isStarted = isRuning
        }
        /**
         * 放入解码器
         * 这里 BaseVideoDecoder 和 BaseVideoEncoder 虽然拆开
         * 但是本质可以写到一起，
         * 他们之间的关联主要是通过surface关联
         * 绘制是绘制到 mediaCodec 创建的surface上
         *
         */
        fun enqueue(data: ByteArray,timeoutUs:Long ,flag: Int) {
            if (mConfigured && isStarted) {
                try {
                    decodeLock.lock()
                    // 从输入缓冲区队列中取出可用缓冲区，并填充数据
                    val index = mMediaCodec?.dequeueInputBuffer(timeoutUs)
                    if (index!! >= 0) {
                        val buffer: ByteBuffer?
                        // since API 21 we have new API to use
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            buffer = mMediaCodec?.getInputBuffers()?.get(index)
                            buffer!!.clear()
                        } else {
                            buffer = mMediaCodec?.getInputBuffer(index)
                        }
                        // 并填充数据
                        if (buffer != null) {
                            buffer.put(data, 0, data.size)
                            mMediaCodec?.queueInputBuffer(index, 0, data.size, timeoutUs, flag)
                        }
                    }
                    decodeLock.unlock()
                } catch (error:Exception){
                    LogHelper.e(TAG,"enqueue:"+error?.message)
                }
            }
        }

        override fun start() {
            super.start()
        }

        override fun interrupt() {
            super.interrupt()
            isStarted = false
        }

        /**
         * 线程编码
         */
        override fun run() {
            LogHelper.d(TAG,"run ---> isStarted = $isStarted" +
                    ", mMediaCodec = $mMediaCodec")
            try {
                val info = MediaCodec.BufferInfo()
//                val outBuffers = mMediaCodec?.getOutputBuffers()
                while (isStarted) {
                    if (mConfigured) {
                        // 从输出缓冲区队列中拿到编解码后的内容，进行相应操作后释放，供下一次使用
                        val index = mMediaCodec?.dequeueOutputBuffer(info, mTimeoutUs)
//                        LogHelper.d(TAG,"run --->   mMediaCodec = $mMediaCodec")
                        if (index!! >= 0) {
//                            val byteBuffer = outBuffers!![index]
//                            byteBuffer.position(info.offset)
//                            byteBuffer.limit(info.offset + info.size)
//                            val byteArray = ByteArray(info.size)
//                            byteBuffer.get(byteArray)
//                            baseVideoEncoder?.onVideoEncode(byteBuffer, info)
//                             setting true is telling system to render frame onto Surface
                            ////使用默认的时间戳渲染视频
                            //使用surface做为输出时与使用Bytebuffer基本一致，只是在surface模式下所有的bytebuffer和image全部为null。
                            mMediaCodec?.releaseOutputBuffer(index, true)
                            if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                                break
                            }
                        }
                        Thread.sleep(10)
                    } else {
                        // just waiting to be configured, then decode and render
                        try {
                            Thread.sleep(10)
                        } catch (ignore: InterruptedException) {
                        }

                    }
                }
                release()
            } finally {
                release()
            }
        }

        public  fun release() {
            if (mConfigured && !isStarted) {
                mMediaCodec?.stop()
                mMediaCodec?.release()
                mConfigured = false
                LogHelper.e(TAG,"释放编解码器")
            }
        }

    }
}