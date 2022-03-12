package com.tao.camera.sample

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.tao.camera.R
import com.tao.camera.callback.ICameraOpenListener
import com.tao.camera.widget.RecordView
import com.tao.common.LogHelper
import com.tao.common.base.BaseActivity
import java.io.File

/**
 * <pre>
 *     author  : devyk on 2020-07-08 14:52
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoRecordingActivity 视频编码录制
 * </pre>
 */
public class VideoRecordingActivity : BaseActivity<Int>(), ICameraOpenListener {

    val tag = "VideoRecordingActivity"
    lateinit var recordView: RecordView
    lateinit var mMp4Path: String //= "sdcard/avsample/record_video.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dir =  getExternalFilesDir(null)?.absolutePath
        mMp4Path = "$dir/record_video.mp4"

        LogHelper.d(tag, "onCreate path = $mMp4Path")
        val file = File(mMp4Path)
        if(file.exists()) {
            file.delete()
        }
    }

    override fun initListener() {

    }

    override fun initData() {
    }

    override fun onContentViewBefore() {
        super.onContentViewBefore()
    }

    override fun init() {
        recordView = findViewById(R.id.recording)
        recordView.addCameraOpenCallback(this)
    }


    override fun getLayoutId(): Int = R.layout.activity_video_recording


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recordView.previewAngle(this)
    }
    /**
     * 开始录制
     */
    fun start_record(view: View) {
        recordView.start()
    }

    /**
     * 开始录制
     */
    fun stop_record(view: View) {
        recordView.stop()
    }

    /**
     * camera 打开的回调
     */
    override fun onCameraOpen() {
        FileUtils.createFileByDeleteOldFile(mMp4Path)
        recordView.setDataSource(mMp4Path)
    }

    override fun onDestroy() {
        super.onDestroy()
        recordView.stop()
        recordView.releaseCamera()
    }
}