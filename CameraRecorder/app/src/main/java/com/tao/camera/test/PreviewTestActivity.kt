/**
 * @ClassName:      PreviewTestActivity
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2022/3/12 3:57 下午
 */
package com.tao.camera.test

import com.blankj.utilcode.util.FileUtils
import com.tao.camera.R
import com.tao.camera.callback.ICameraOpenListener
import com.tao.common.base.BaseActivity

class PreviewTestActivity: BaseActivity<Int>() {

    lateinit var  myGLSurfaceView:MyGLSurfaceView
    override fun initListener() {

    }

    override fun initData() {
    }

    override fun init() {
        myGLSurfaceView = findViewById(R.id.mygl_camera_view)
//        myGLSurfaceView.addCameraOpenCallback(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_preview_test
    }

    /**
     * camera 打开的回调
     */
//    override fun onCameraOpen() {
//        FileUtils.createFileByDeleteOldFile(mMp4Path)
//        recordView.setDataSource(mMp4Path)
//    }

    override fun onDestroy() {
        super.onDestroy()
//        myGLSurfaceView.releaseCamera()
    }
}