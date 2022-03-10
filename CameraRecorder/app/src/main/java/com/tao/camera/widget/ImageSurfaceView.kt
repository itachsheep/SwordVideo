package com.tao.camera.widget

import android.content.Context
import android.util.AttributeSet
import com.tao.camera.RendererConfiguration
import com.tao.camera.egl.render.ImageRenderer


/**
 * <pre>
 *     author  : devyk on 2020-07-06 22:13
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BitmapSurface 绘制一张图片
 * </pre>
 */
public abstract class ImageSurfaceView : GLSurfaceView, ImageRenderer.OnRendererListener {


    private var mFboTextureId = -1;
    override fun onCreate(textureId: Int) {
        mFboTextureId = textureId
        onInitSuccess()
    }

    public abstract fun onInitSuccess()

    private var mBitmapRenderer: ImageRenderer? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mBitmapRenderer = ImageRenderer(context!!)
        configure(RendererConfiguration.Builder().setRenderer(mBitmapRenderer!!).setRendererMode(RENDERERMODE_WHEN_DIRTY).build())
        mBitmapRenderer?.setOnRendererListener(this)

    }

    public fun setImagePath(path: String?) {
        mBitmapRenderer?.let {
            mBitmapRenderer?.setImagePath(path)
            requestRenderer()
        }
    }

    fun getTextureId(): Int = mFboTextureId
}