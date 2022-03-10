package com.tao.camera.sample

import android.content.res.Configuration
import com.tao.camera.R
import com.tao.camera.widget.CameraView
import com.tao.common.base.BaseActivity

/**
 * <pre>
 *     author  : devyk on 2020-07-04 14:45
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is CameraPreviewActivity 相机预览
 * </pre>
 */

public class CameraPreviewActivity : BaseActivity<Int>() {
    lateinit var camera_view: CameraView

    override fun initListener() {
    }

    override fun initData() {
        camera_view = findViewById(R.id.camera_view)
    }

    override fun init() {
    }

    override fun onContentViewBefore() {
        super.onContentViewBefore()
        setNotTitleBar()
    }

    override fun getLayoutId(): Int = R.layout.activity_camera


    override fun onDestroy() {
        super.onDestroy()
        camera_view.releaseCamera()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        camera_view.previewAngle(this)
    }
}