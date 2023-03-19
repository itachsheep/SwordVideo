package com.tw.speedbrowser.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.webkit.WebView;

import com.tw.speedbrowser.base.common.LanguageUtils;
import com.tw.speedbrowser.base.utils.LogUtils;
import com.tw.speedbrowser.base.utils.ThemeModeUtils;

import io.reactivex.plugins.RxJavaPlugins;

import com.tw.speedbrowser.manager.FavorManager;
import com.tw.speedbrowser.manager.RecentSearchManager;

/**
 * 全局的Application
 */
public class SpeedApplication extends BaseApplication {

    private String TAG = "SpeedApplication";
    protected static SpeedApplication INSTANCE;

    public Context mContext;

    public static SpeedApplication getApp() {
        if (INSTANCE == null) {
            INSTANCE = new SpeedApplication();
            INSTANCE.onCreate();
        }
        return INSTANCE;
    }

    public boolean isAppInBackground;
    private int appCount;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        INSTANCE = this;


        //初始化主题模式
        ThemeModeUtils.initThemeMode(getApp());

        initBackgroundCallBack();

        initOther();

        RecentSearchManager.INSTANCE.init();
        FavorManager.INSTANCE.init();


    }


    private void initOther(){
        Thread threadOther = new Thread(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
            //initShareSDK(INSTANCE);
        });
        threadOther.start();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initWebView();
    }


    private static final String PROCESS = "pro.bingbon.app";

    private void initWebView() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                String processName = getProcessName(this);
                if (!PROCESS.equals(processName) || !isEmpty(processName)) {
                    WebView.setDataDirectorySuffix(getString(processName, "bingbon"));
                }
            }
        } catch (Exception e) {

        }

    }

    public String getString(String s, String defValue) {
        return isEmpty(s) ? defValue : s;
    }

    public boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public String getProcessName(Context context) {
        try {
            if (context == null) return null;
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                if (processInfo.pid == Process.myPid()) {
                    return processInfo.processName;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private long mRecordStartTime = 0;
    private void initBackgroundCallBack() {

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                AppManager.getINSTANCE().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (appCount == 0) {
                    mRecordStartTime = System.currentTimeMillis();
                }
                appCount++;
                mActivityCount++;
                BaseApplication.getApp().setActivityCount(mActivityCount);
                if (isAppInBackground) {
                    //应用从后台回到前台 需要做的操作
                    back2App(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                appCount--;
                mActivityCount--;
                BaseApplication.getApp().setActivityCount(mActivityCount);
                if (appCount == 0) {
                    //应用进入后台 需要做的操作
                    leaveApp(activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppManager.getINSTANCE().finishActivity(activity);
            }
        });

    }

    private int mActivityCount = 0;

    public int getActivityCount() {
        return mActivityCount;
    }

    public boolean isAlive() {
        return getActivityCount() > 0;
    }

    public boolean isBackground() {
        return getActivityCount() <= 0;
    }


    /**
     * 从后台回到前台需要执行的逻辑
     *
     * @param activity Activity
     */
    private void back2App(Activity activity) {
        Long backgroundBackInterrupt = (System.currentTimeMillis() - mRunInBackgroundTime) / (1000 * 60);
        if (backgroundBackInterrupt > 30) {
            // 如果后台超过了30分钟  火币也差不多  那就重新重启下app  防止华为手机的系统问题
            // 然后这个标志位置未当前的时间戳  防止重启死循环
            mRunInBackgroundTime = System.currentTimeMillis();
//            EnterClass.restartApp();
            return;
        }

        isAppInBackground = false;
    }

    private Long mRunInBackgroundTime = 0L;

    /**
     * 离开应用 压入后台或者退出应用
     *
     * @param activity Activity
     */
    private void leaveApp(Activity activity) {
        mRunInBackgroundTime = System.currentTimeMillis();
        isAppInBackground = true;
    }


    public static SpeedApplication getInstance() {
        return INSTANCE;
    }

}
