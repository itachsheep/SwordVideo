/**
 * @ClassName:      MyGLSurfaceView
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2022/3/12 4:04 下午
 */
package com.tao.camera.test

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.tao.camera.RendererConfiguration
import com.tao.camera.callback.ICameraOpenListener
import com.tao.camera.callback.IGLThreadConfig
import com.tao.camera.egl.EglHelper
import com.tao.camera.egl.render.CameraRenderer
import com.tao.camera.widget.GLSurfaceView
import com.tao.common.LogHelper
import com.tao.common.camera.CameraHolder
import com.tao.common.config.CameraConfiguration
import java.lang.ref.WeakReference

class MyGLSurfaceView: SurfaceView, SurfaceHolder.Callback, SurfaceTexture.OnFrameAvailableListener {

    val TAG = "MyGLSurfaceView"
    public var mSurface: Surface? = null
    /**
     * GLES 渲染线程
     */
    private lateinit var mEglThread: MyGlThread

    /**
     * Camera 渲染器
     */
    protected lateinit var renderer: CameraRenderer
    /**
     * 默认后置摄像头
     */
    private var cameraId = CameraConfiguration.Facing.BACK
    /**
     * 相机预览的纹理 ID
     */
    protected var mTextureId = -1;
    protected var mCameraOpenListener: ICameraOpenListener? = null

    protected var mRenderMode = GLSurfaceView.RENDERERMODE_CONTINUOUSLY


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        holder.addCallback(this)

        renderer = CameraRenderer(context!!)
        mRenderMode = GLSurfaceView.RENDERERMODE_CONTINUOUSLY
        //第一次需要初始化预览角度
        previewAngle(context)
        addRendererListener()
        LogHelper.d(TAG,"constructor")
    }



    public fun addCameraOpenCallback(listener:ICameraOpenListener){
        mCameraOpenListener = listener
    }

    private fun addRendererListener() {
        renderer.setOnRendererListener(object : CameraRenderer.OnRendererListener {
            override fun onCreate(cameraTextureId: Int, textureID: Int) {
                mTextureId = textureID
                val cameraConfiguration =
                    CameraConfiguration.Builder().setFacing(cameraId).build()
                CameraHolder.instance().setConfiguration(cameraConfiguration)
                CameraHolder.instance().openCamera()
                //todo： 很重要
                // 将 CameraRender 生成的纹理 给到CameraHolder，
                // CameraHolder 设置预览的纹理，
                // 完成将纹理数据给到 GLSurfaceView 的 CameraRender中
                CameraHolder.instance().setSurfaceTexture(cameraTextureId,
                    this@MyGLSurfaceView);
                CameraHolder.instance().startPreview();
                LogHelper.e(TAG,"onCreate TextureId:${mTextureId}")
                mCameraOpenListener?.onCameraOpen()
            }

            override fun onDraw() {
                CameraHolder.instance().updateTexImage()
            }
        })
    }


    fun previewAngle(context: Context) {
        val rotation =
            (context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.rotation
        LogHelper.d(TAG, "旋转角度：" + rotation)
        renderer.resetMatrix()
        when (rotation) {

            Surface.ROTATION_0 -> {
                if (cameraId == CameraConfiguration.Facing.BACK) {
                    renderer.setAngle(90, 0, 0, 1);
                    renderer.setAngle(180, 1, 0, 0);
                } else {
                    renderer.setAngle(90, 0, 0, 1);
                }
            }

            Surface.ROTATION_90 -> {
                if (cameraId == CameraConfiguration.Facing.BACK) {
                    renderer.setAngle(180, 0, 0, 1);
                    renderer.setAngle(180, 0, 1, 0);
                } else {
                    renderer.setAngle(90, 0, 0, 1);
                }
            }

            Surface.ROTATION_180 -> {
                if (cameraId == CameraConfiguration.Facing.BACK) {
                    renderer.setAngle(90, 0, 0, 1);
                    renderer.setAngle(180, 0, 1, 0);
                } else {
                    renderer.setAngle(-90, 0, 0, 1);
                }
            }

            Surface.ROTATION_270 -> {
                if (cameraId == CameraConfiguration.Facing.BACK) {
                    renderer.setAngle(180, 0, 1, 0);
                } else {
                    renderer.setAngle(0, 0, 0, 1);
                }
            }
        }
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        LogHelper.d(TAG,"surfaceCreated 11")
        if (mSurface == null) {
            mSurface = holder.surface
        }
        this.mEglThread = MyGlThread()
        this.mEglThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mEglThread.let { eglThread ->
            eglThread.setRendererSize(width, height)
            eglThread.isChange = true
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mEglThread.let {
//            mEglThread.onDestory()
        }
    }


    inner class MyGlThread: Thread() {
        val TAG = "MyGlThread"
        /**
         * EGL 环境搭建帮助类
         */
        protected lateinit var mEGLHelper: EglHelper
        /**
         * 刷新帧率
         */
        private var mDrawFpsRate = 60L

        /**
         * 渲染的 size
         */
        private var mWidth = 1080
        private var mHeight = 1920;
        /**
         * 窗口是否改变
         */
        internal var isChange = false

        /**
         * 渲染窗口的大小
         */
        public fun setRendererSize(width: Int, height: Int) {
            this.mWidth = width
            this.mHeight = height
        }


        /**
         * 渲染器需要改变窗口大小
         */
        private fun onChange(width: Int, height: Int) {
//            if (!isChange) return
//            this.isChange = false
            renderer?.onSurfaceChange(width, height)
        }

        /**
         * 对象锁
         */
        private val mLock = Object()

        override fun run() {
            LogHelper.d(TAG,"run ----> ")
            //实例化 EGL 环境搭建的帮组类
            mEGLHelper = EglHelper()
            mEGLHelper.initEgl(mSurface, mEGLHelper.getEglContext())
            LogHelper.d(TAG,"run ---- 1")
            while (true) {
                LogHelper.d(TAG,"run ---- 2")
                try {
                    sleep(500)
                   // continue
                } catch (error: InterruptedException) {
                    LogHelper.e(TAG, error.message)
                }
                LogHelper.d(TAG,"run mRenderMode = " + mRenderMode)
                //判断是手动刷新还是自动 刷新
                if (mRenderMode == GLSurfaceView.RENDERERMODE_WHEN_DIRTY) {
                    synchronized(mLock) {
                        try {
                            mLock.wait()
                        } catch (error: InterruptedException) {
                            LogHelper.e(TAG, error.message)
                        }
                    }

                } else if (mRenderMode == GLSurfaceView.RENDERERMODE_CONTINUOUSLY) {
                    try {
                        sleep(1000 / mDrawFpsRate)
                    } catch (error: InterruptedException) {
                        LogHelper.e(TAG, error.message)
                    }
                } else {
                    throw RuntimeException("mRendererMode is wrong value");
                }
                LogHelper.d(TAG,"run onCreate  ")
                //开始创建
                onCreate(mWidth, mHeight)
                //改变窗口
                onChange(mWidth, mHeight)
                //开始绘制
                onDraw()
//                this.isStart = true
            }
        }

        /**
         * 渲染器可以创建了
         */
        private fun onCreate(width: Int, height: Int) {
            LogHelper.d(TAG,"onCreate width = " + width + ", heigth = " + height)
            renderer?.onSurfaceCreate(width, height)
        }

        /**
         * 渲染器可以开始绘制了
         */
        private fun onDraw() {
            renderer?.onDraw()
//            if (!isStart)
//                view.getRenderer()?.onDraw()
            this.mEGLHelper.swapBuffers()
        }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {

    }
}