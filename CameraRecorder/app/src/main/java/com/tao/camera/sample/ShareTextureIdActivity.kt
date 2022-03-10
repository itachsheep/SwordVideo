package com.tao.camera.sample

import android.view.ViewGroup
import android.widget.LinearLayout
import com.tao.camera.R
import com.tao.camera.egl.render.BitmapRenderer
import com.tao.camera.widget.BitmapSurfaceView
import com.tao.camera.widget.MultiSurfaceView
import com.tao.common.base.BaseActivity

/**
 * <pre>
 *     author  : devyk on 2020-07-06 10:23
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is ShareTextureIdActivity 共享纹理 ID
 * </pre>
 */
public class ShareTextureIdActivity : BaseActivity<Int>() {

    lateinit var bitmap_surface: BitmapSurfaceView
    lateinit var content: LinearLayout

    override fun initListener() {
    }

    override fun initData() {
        bitmap_surface = findViewById(R.id.bitmap_surface)
        content = findViewById(R.id.content)

    }

    override fun init() {
        bitmap_surface.getRenderer()?.setOnRendererListener(object : BitmapRenderer.OnRendererListener {
            override fun onCreate(textureId: Int) {
                runOnUiThread {
                    if (content.childCount > 0) {
                        content.removeAllViews()
                    }
                    for (index in 0..2) {
                        val eglContext = bitmap_surface.getEGLContext()
                        if(eglContext == null)continue
                        var surfaceView =
                            MultiSurfaceView(applicationContext)
                        surfaceView.config(eglContext)
                        surfaceView?.setTextureId(textureId, index)

                        var layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        layoutParams.width = 0
                        layoutParams.weight = 1f
                        surfaceView.layoutParams = layoutParams
                        content.addView(surfaceView)
                    }
                }
            }
        })

    }

    override fun onContentViewBefore() {
        super.onContentViewBefore()
        setNotTitleBar()
    }

    override fun getLayoutId(): Int = R.layout.activity_share_texture_id
}