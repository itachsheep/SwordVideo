package com.tao.camera.sample

import android.content.res.Configuration
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.tao.camera.R
import com.tao.camera.callback.ICameraOpenListener
import com.tao.camera.widget.RecordView
import com.tao.common.base.BaseActivity

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


    lateinit var recording: RecordView
    protected var mMp4Path = "sdcard/avsample/record_video.mp4"

    override fun initListener() {

    }

    override fun initData() {
    }

    override fun onContentViewBefore() {
        super.onContentViewBefore()
    }

    override fun init() {
        recording = findViewById(R.id.recording)
        recording.addCameraOpenCallback(this)
    }


    override fun getLayoutId(): Int = R.layout.activity_video_recording


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recording.previewAngle(this)
    }
    /**
     * 开始录制
     */
    fun start_record(view: View) {
        recording.start()
    }

    /**
     * 开始录制
     */
    fun stop_record(view: View) {
        recording.stop()
    }

    /**
     * camera 打开的回调
     */
    override fun onCameraOpen() {
        FileUtils.createFileByDeleteOldFile(mMp4Path)
        recording.setDataSource(mMp4Path)
    }

    override fun onDestroy() {
        super.onDestroy()
        recording.stop()
        recording.releaseCamera()
    }
}