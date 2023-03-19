package com.tw.speedbrowser.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.EmptyStackException;
import java.util.Stack;


public class AppManager {

    private Stack<Activity> activityStack = new Stack<>();

    private Stack<Activity> needFinishStack = new Stack<>();

    private static AppManager INSTANCE;

    private AppManager() {
    }

    public static AppManager getINSTANCE() {
        synchronized (AppManager.class) {
            if (INSTANCE == null) {
                INSTANCE = new AppManager();
            }
        }
        return INSTANCE;
    }

    public int getActivityStackSize() {
        return activityStack.size();
    }

    /**
     * 压栈
     */
    public void addActivity(Activity activity) {
//        Log.d("AppManager", activity.getClass().getName());
        activityStack.add(activity);
    }


    /**
     * 出栈
     */
    public boolean popActivity() {

        //因为还有一个启动界面  这里不要忽略哦
        if (activityStack.size() <= 1) {
            return false;
        } else {
            try {
                if (activityStack.get(0).getClass().getName().contains("SplashActivity")) {

                } else {
                    activityStack.get(activityStack.size() - 1).finish();
                    activityStack.pop();
                }
            } catch (EmptyStackException e) {

            }

            return true;
        }
    }


    public void finishStillMainActivity() {
        for (Activity activity : activityStack) {
            if (!activity.getComponentName().getClassName().contains("MainActivity")) {
                activity.finish();
            }
        }
        if (!activityStack.isEmpty()) {
            // 这里要取第一位
            Activity a = activityStack.firstElement();
            activityStack.clear();
            activityStack.add(a);
        }
    }

    /**
     * 移除activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    /**
     * 当前栈顶的activity
     */
    public Activity currentActivity() {
        if (activityStack.empty()) {
            return null;
        }
        return activityStack.lastElement();
    }


    /**
     * 清理栈
     */
    public void finishAllActivity() {
        for (Activity activity : activityStack)
            activity.finish();

        activityStack.clear();
    }

    /**
     * 清理栈
     */
    public Activity getFistActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.firstElement();
    }


    /**
     * 退出应用程序
     */
    public void exitApp(Context context) {
        finishAllActivity();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        activityManager.killBackgroundProcesses(context.getPackageName());
        System.exit(0);
    }


    public Activity getTopActivity() {
        if (!activityStack.isEmpty()) {
            Activity topActivity = activityStack.get(activityStack.size() - 1);

            if (isActivityAlive(topActivity)) {
                return topActivity;
            }
            if (activityStack.size() > 1) {
                Activity topSecondActivity = activityStack.get(activityStack.size() - 2);
                if (isActivityAlive(topSecondActivity)) {
                    return topSecondActivity;
                }
            }
        }

        return null;
    }


    /**
     * Return whether the activity is alive.
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isActivityAlive(final Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }
}
