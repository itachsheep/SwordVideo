package com.tao.camera.widget

import android.content.Context
import android.util.AttributeSet
import com.tao.camera.packer.MP4Packer

/**
 * <pre>
 *     author  : devyk on 2020-07-11 23:40
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is ImageVideoView
 * </pre>
 */
public class ImageVideoView : ImageSurfaceView {

    private var mListener: OnImageVideoInitListener? = null


    override fun onInitSuccess() {
        mListener?.onInitSuccess()
    }

    private var mMP4Packer: MP4Packer? = null

    protected var mContext: Context? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
    }

    public fun start() {
        mMP4Packer?.start()
    }

    public fun stop() {
        mMP4Packer?.stop()

    }

    public fun pause() {
        mMP4Packer?.pause()
    }

    public fun resume() {
        mMP4Packer?.resume()
    }

    fun setDataSource(path: String) {
        mMP4Packer = getEGLContext()?.let { MP4Packer(context.applicationContext, getTextureId(), it, path) }
    }


    public interface OnImageVideoInitListener {
        fun onInitSuccess()
    }

    public fun setOnImageViewInitListener(listener: OnImageVideoInitListener) {
        mListener = listener

    }

}