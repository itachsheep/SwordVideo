package com.tw.speedbrowser.base.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.tw.speedbrowser.base.BaseApplication;



/**
 * 版本相关
 */
public class VersionUtils {

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String mVersion;

    public static String getVersion(Context context) {
        try {
            if (TextUtils.isEmpty(mVersion)) {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                mVersion = info.versionName;
            }
            return mVersion.split("_")[0];
        } catch (Exception e) {
            e.printStackTrace();
            return "2.12.0";
        }
    }

    public static String getVersion() {
        try {
            if (TextUtils.isEmpty(mVersion)) {
                PackageManager manager = BaseApplication.getApp().getPackageManager();
                PackageInfo info = manager.getPackageInfo(BaseApplication.getApp().getPackageName(), 0);
                mVersion = info.versionName;
            }
            return mVersion.split("_")[0];
        } catch (Exception e) {
            e.printStackTrace();
            return "2.12.0";
        }
    }

    public static String getRealVersion(Context context) {
        try {
            if (TextUtils.isEmpty(mVersion)) {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                mVersion = info.versionName;
            }
            return mVersion;
        } catch (Exception e) {
            e.printStackTrace();
            return "2.12.0";
        }
    }

    /**
     * 如果版本1 大于 版本2 返回true 否则返回fasle 支持 2.2 2.2.1 比较
     * 支持不同位数的比较  2.0.0.0.0.1  2.0 对比
     *
     * @param serviceVersion 版本服务器版本 " 1.1.2 "
     * @param currentVersion 版本 当前版本 " 1.2.1 "
     * @return true ：需要更新 false ： 不需要更新
     */
    public static boolean compareVersions(String serviceVersion, String currentVersion) {

        //判断是否为空数据
        if (TextUtils.equals(serviceVersion, "") || TextUtils.equals(currentVersion, "")) {
            return false;
        }
        String[] str1 = serviceVersion.split("\\.");
        String[] str2 = currentVersion.split("\\.");

        try {
            if (str1.length == str2.length) {
                for (int i = 0; i < str1.length; i++) {
                    if (Integer.parseInt(str1[i]) > Integer.parseInt(str2[i])) {
                        return true;
                    } else if (Integer.parseInt(str1[i]) < Integer.parseInt(str2[i])) {
                        return false;
                    } else if (Integer.parseInt(str1[i]) == Integer.parseInt(str2[i])) {

                    }
                }
            } else {
                if (str1.length > str2.length) {
                    for (int i = 0; i < str2.length; i++) {
                        if (Integer.parseInt(str1[i]) > Integer.parseInt(str2[i])) {
                            return true;
                        } else if (Integer.parseInt(str1[i]) < Integer.parseInt(str2[i])) {
                            return false;

                        } else if (Integer.parseInt(str1[i]) == Integer.parseInt(str2[i])) {
                            if (str2.length == 1) {
                                continue;
                            }
                            if (i == str2.length - 1) {

                                for (int j = i; j < str1.length; j++) {
                                    if (Integer.parseInt(str1[j]) != 0) {
                                        return true;
                                    }
                                    if (j == str1.length - 1) {
                                        return false;
                                    }

                                }
                                return true;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < str1.length; i++) {
                        if (Integer.parseInt(str1[i]) > Integer.parseInt(str2[i])) {
                            return true;
                        } else if (Integer.parseInt(str1[i]) < Integer.parseInt(str2[i])) {
                            return false;

                        } else if (Integer.parseInt(str1[i]) == Integer.parseInt(str2[i])) {
                            if (str1.length == 1) {
                                continue;
                            }
                            if (i == str1.length - 1) {
                                return false;

                            }
                        }

                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean jumpPlayMarket(final Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=pro.bingbon.app"));
            intent.setPackage("com.android.vending");//这里对应的是谷歌商店
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                //没有应用市场，通过浏览器跳转到Google Play
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=pro.bingbon.app"));
                if (intent2.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent2);
                } else {
                    //没有Google Play 也没有浏览器
                    return false;
                }
            }
            return true;
        } catch (Exception activityNotFoundException1) {
            return false;
        }
    }


}
