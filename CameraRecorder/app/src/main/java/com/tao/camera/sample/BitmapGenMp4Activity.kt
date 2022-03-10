package com.tao.camera.sample

import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.tao.camera.R
import com.tao.camera.widget.ImageVideoView
import com.tao.common.FindFiles
import com.tao.common.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * <pre>
 *     author  : devyk on 2020-07-11 19:57
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BitmapGenMp4Activity
 * </pre>
 */
public class BitmapGenMp4Activity : BaseActivity<Int>(), ImageVideoView.OnImageVideoInitListener,
    CoroutineScope {

    lateinit var job: Job

    private var mImageViewPath = "sdcard/avsample/image_gen_video.mp4"
    private var mLists: MutableList<String>? = null

    lateinit var image_surfaceview:ImageVideoView
    override fun onInitSuccess() {
        image_surfaceview.setDataSource(mImageViewPath)

        launch(Dispatchers.IO) {
            getImage()
        }
    }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun initListener() {

    }

    override fun initData() {
        image_surfaceview = findViewById(R.id.image_surfaceview)
        image_surfaceview.setOnImageViewInitListener(this)
    }

    override fun init() {
        job = Job()
    }

    override fun getLayoutId(): Int = R.layout.activity_bitmap_gen_mp4

    fun getImage() {
        val findFiles = FindFiles()
        findFiles.queryFiles(File("sdcard"), FindFiles.FILE_TYPE.JPG, object : FindFiles.IFindFileCallback {
            override fun onFiles(lists: MutableList<String>?) {
                mLists = lists
                mLists?.run {
                    if (size > 0) {
                        var index = 0;
                        while (true) {
                            if (index >= size) index = 0
                            image_surfaceview.setImagePath(get(index++))
                            Thread.sleep(1000 / 60)
                        }
                    }
                }
            }

            override fun onError(error: String?) {
                ToastUtils.showShort(error)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        image_surfaceview.stop()
    }


    fun start_record(view: View) {
        image_surfaceview.start()

    }

    fun stop_record(view: View) {
        image_surfaceview.stop()
    }


}