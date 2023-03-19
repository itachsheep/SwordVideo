package com.tw.speedbrowser.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.tw.speedbrowser.R;
import com.tw.speedbrowser.base.ui.BaseNewActivity;
import com.freddy.event.I_CEventListener;
import com.freddy.event.CEventCenter;
import com.tw.speedbrowser.base.ui.immersionbar.ImmersionBar;
import com.tw.speedbrowser.base.widget.LoadingDialog;
import com.tw.speedbrowser.base.common.LanguageUtils;
import com.tw.speedbrowser.base.utils.*;

/**
 * 所有Activity的基类
 */
public abstract class BaseActivity extends BaseNewActivity implements I_CEventListener {

    public static final String NET_OF_WIFI = "wifi";
    public static final String NET_OF_4G = "4G";
    public static final String NET_OF_NO_NET = "no net";
    public static final String NET_OF_2G = "2G";
    public static final String MAIN_NAME = "MainActivity";
    public static final String MAIN_URI = "bingbon://pro.bingbon";

    private android.app.Dialog mDialog;
    protected boolean mFinishedOnCreate = false;
    public boolean isReleased = true;

    public void showCusLoading() {
        runOnUiThread(() -> {
            try {
                if (mDialog == null) {
                    mDialog = new LoadingDialog(BaseActivity.this);
                    mDialog.setCancelable(true); // true设置返回取消
                    mDialog.setCanceledOnTouchOutside(true);
                } else {
                    mDialog.dismiss();
                }
                Activity currentActivity = AppManager.getINSTANCE().currentActivity();
                if (currentActivity != null && !currentActivity.isFinishing() && !mDialog.isShowing()) {
                    mDialog.show();
                }
            } catch (Exception e) {

            }
        });

    }

    public void onLoading(boolean loading) {
        runOnUiThread(() -> {
            try {
                Activity currentActivity = AppManager.getINSTANCE().currentActivity();
                if (currentActivity != null && !currentActivity.isFinishing()) {
                    if (loading) showCusLoading();
                    else hideLoading();
                }
            } catch (Exception e) {

            }
        });
    }

    @Override
    protected void onResume() {
//        MobclickAgent.onResume(this);
        super.onResume();
    }


    @Override
    protected void onPause() {
//        MobclickAgent.onPause(this);
        super.onPause();
    }


    public void hideLoading() {
        runOnUiThread(() -> {
            try {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            } catch (Exception e) {
                mDialog = null;
            } finally {
                mDialog = null;
            }

        });
    }

    public ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        OOMHelper.INSTANCE.put(this);
        isReleased = false;
        mFinishedOnCreate = false;
        //全局竖屏
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayout());
        initArgs(getIntent().getExtras());
//        fullScreen2StatusBar(this);
        initNewImmersionBar();
        initView();
        initListener();
        initData();
        String[] eventCenterTopics = getEventCenterTopics();
        if (eventCenterTopics != null && eventCenterTopics.length > 0) {
            CEventCenter.registerEventListener(this, eventCenterTopics);
        }
        mFinishedOnCreate = true;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        //app杀进程启动后会调用Activity attachBaseContext
        LogUtils.INSTANCE.d(getClass().getSimpleName(),"attachBaseContext ->");
        LanguageUtils.getInstance().setConfiguration(newBase);
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    protected void back() {
        CommonUtils.closeKeyBoard(this);
        finish();
        //Logger.e("-------- 当前还有多少个activity---" + AppManager.getINSTANCE().getActivityStackSize());
    }


    private void initNewImmersionBar() {
        boolean isNight = ThemeModeUtils.isNight(this);
        mImmersionBar = ImmersionBar.with(this);
        boolean statusFont = false;
        //日间模式，白色主题
        if (!isNight) {
            statusFont = true;
        }
        mImmersionBar
                .fitsSystemWindows(true)
                .statusBarColor(R.color.style_statusBar_white_FFFFFF)
                .statusBarDarkFont(statusFont, 0.2f)
                .navigationBarColor(R.color.style_statusBar_white_FFFFFF)
                .navigationBarDarkIcon(true)
                .init();
    }


    // 通过updateConfiguration方法修改Resource的Locale,连带修改Resource内Asset的Local.
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        // 防止多次调用，应该首次就OK   为了防止系统字体大小调整之后，app显示异常
        if (res.getConfiguration().fontScale != 1.0F) {
            Configuration config = new Configuration();
            config.setToDefaults();
            // 这个要把本次的语言也更新下  也就是xml  resource里面的也能更新到
            config.locale = LanguageUtils.getInstance().getLanguageLocale(this);
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return res;
    }

    /**
     * 默认实现沉浸式状态栏的
     * true为需要根据设置的主题模式适配
     * false为一直都是黑色模式
     *
     * @return true / false
     */
    protected boolean isNewImmersionBareEnabled() {
        return true;
    }

    /**
     * 默认实现沉浸式状态栏的
     * <p>
     * 需要实现布局上移到状态栏，或者其他需求的可以实现 @return false 自己实现
     *
     * @return true / false
     */
    protected boolean hasImmersionBare() {
        return true;
    }

    /**
     * 默认实现沉浸式状态栏的
     * <p>
     * 需要实现布局上移到状态栏，或者其他需求的可以实现 @return false 自己实现
     *
     * @return true / false  用于3.0之后的详情页面
     */
    protected boolean hasNewVersionImmersionBare() {
        return false;
    }


    /**
     * 用于初始化数据请求
     */
    protected abstract void initData();

    /**
     * 用于设置控件的监听事件
     */
    protected abstract void initListener();

    /**
     * 用于View的绑定
     */
    protected abstract void initView();

    /**
     * 获取到布局id
     *
     * @return layoutId
     */
    public abstract @LayoutRes
    int getLayout();

    /**
     * 初始化窗口
     *
     * @param bundle 数据
     * @return 如果初始化成功  返回true
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }




    @Override
    protected void onDestroy() {
        if (mDialog != null) {
            ImmersionBar.destroy(this, mDialog);  //在BaseActivity里销毁
        }

        String[] eventCenterTopics = getEventCenterTopics();
        if (eventCenterTopics != null && eventCenterTopics.length > 0) {
            CEventCenter.unregisterEventListener(this, eventCenterTopics);
        }
        super.onDestroy();
        isReleased = true;
    }


    /**
     * 通过设置全屏，设置状态栏透明
     */
    public static void fullScreen2StatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        if (activity.getWindow() == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //导航栏颜色也可以正常设置
            //window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            window.setAttributes(attributes);
        }
    }








    @CallSuper
    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        if (isReleased) return;
        processEvent(topic, msgCode, resultCode, obj);
    }

    protected void processEvent(String topic, int msgCode, int resultCode, Object event) {
    }

    protected String[] getEventCenterTopics() {
        return null;
    }
}
