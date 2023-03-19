package com.tw.speedbrowser.base;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.Nullable;


import com.tw.speedbrowser.BuildConfig;
import com.tw.speedbrowser.base.utils.CommonUtils;
import com.tw.speedbrowser.base.utils.SPUtils;
import com.tw.speedbrowser.base.utils.UUIDUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class BaseApplication extends Application {


    protected static BaseApplication INSTANCE;

    public Context mContext;

    public static BaseApplication getApp() {
        if (INSTANCE == null) {
            INSTANCE = new BaseApplication();
            INSTANCE.onCreate();
        }
        return INSTANCE;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        INSTANCE = this;

        initLogger();

        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt == Build.VERSION_CODES.O) {
            hookWebView(sdkInt);
        }
    }

    private int mActivityCount;

    public int activityCount() {
        return mActivityCount;
    }

    public boolean isRunBackground() {
        return activityCount() <= 0;
    }

    public void setActivityCount(int mActivityCount) {
        this.mActivityCount = mActivityCount;
    }


    /**
     * 初始化Logger的相关配置
     */
    private void initLogger() {
    }

    private static HashMap<String, HashMap<String, Long>> hashMap = new HashMap<>();

    public static void setHashMap(HashMap<String, HashMap<String, Long>> hashMap) {
        BaseApplication.hashMap = hashMap;
    }

    public static HashMap<String, HashMap<String, Long>> getHashMap() {
        return hashMap;
    }

    private static String UNIQUE_ID = "";

    public static String getUniquePsuedoID() {

        // 判断是否为空
        if (TextUtils.isEmpty(UNIQUE_ID)) {
            // 如果为空就从缓存中获取
            UNIQUE_ID = SPUtils.getInstance().getString("device_id", UUIDUtils.getUuid());
            // 然后进行返回
            return UNIQUE_ID;
        }
        // 如果不为空就直接返回
        return UNIQUE_ID;
    }

    public static void setUniqueId(String uniqueId) {

        UNIQUE_ID = uniqueId;
        SPUtils.getInstance().put("device_id", uniqueId);
    }


    private static Map<String, String> tradeMaps = new HashMap<>();


    private Boolean firstSplash = false;

    public static void hookWebView(int sdkInt){//修复android8.0的系统问题

        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
//                Log.i(TAG,"sProviderInstance isn't null");
                return;
            }

            Method getProviderClassMethod;
            if (sdkInt > Build.VERSION_CODES.LOLLIPOP_MR1) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == Build.VERSION_CODES.LOLLIPOP_MR1) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
//                Log.i(TAG,"Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if(sdkInt < Build.VERSION_CODES.O){//低于Android O版本
                Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
                }
            } else {
                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String)chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
                if (staticFactory!=null){
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
                }
            }

            if (sProviderInstance != null){
                field.set("sProviderInstance", sProviderInstance);
//                Log.i(TAG,"Hook success!");
            } else {
//                Log.i(TAG,"Hook failed!");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
