package com.tw.speedbrowser.base.utils;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tw.speedbrowser.base.BaseApplication;
import com.tw.speedbrowser.base.common.LanguageUtils;
import com.tw.speedbrowser.datamanager.ACache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class CommonUtils {
    private static InputMethodManager imm;


    public static void printServiceName() {
        final ActivityManager activityManager = (ActivityManager) BaseApplication.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);

    }

    /**
     * 给字加下划线
     *
     * @param ct
     * @param v
     * @param color
     */
    public static void setUNDERLINE(Context ct, TextView v, int color) {
        v.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        v.setTextColor(ResUtils.getColor(color));
    }

    /**
     * 往手机通讯录插入联系人
     *
     * @param context 上下文
     * @param name    名字
     * @param tel     电话
     * @param email   邮箱
     */
    public static void insertContact(Context context, String name, String tel, String email) {
        ContentValues values = new ContentValues();
        // 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
        Uri rawContactUri = context.getContentResolver().insert(RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        // 往data表入姓名数据
        if (!TextUtils.isEmpty(tel)) {
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);// 内容类型
            values.put(StructuredName.GIVEN_NAME, name);
            context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        }
        // 往data表入电话数据
        if (!TextUtils.isEmpty(tel)) {
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);// 内容类型
            values.put(Phone.NUMBER, tel);
            values.put(Phone.TYPE, Phone.TYPE_MOBILE);
            context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        }
        // 往data表入Email数据
        if (!TextUtils.isEmpty(email)) {
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);// 内容类型
            values.put(Email.DATA, email);
            values.put(Email.TYPE, Email.TYPE_WORK);
            context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        }
    }


    /**
     * 发短信
     *
     * @param context 上下文
     * @param tel     手机号
     */
    public static void sendMessage(Context context, String tel) {
        Intent localIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + tel));
        context.startActivity(localIntent);
    }


    /**
     * 显示软键盘
     *
     * @param view    EditText
     * @param context 上下文
     */
    public static void showSoft(EditText view, Context context) {
        if (null == imm) {
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (view != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 打开键盘
     *
     * @param context 上下文
     */
    public static void openKeyBoard(final Context context) {
        openKeyBoard(context, 300);
    }

    /**
     * 打开键盘
     *
     * @param context 上下文
     * @param delay   延迟（毫秒）
     */
    public static void openKeyBoard(final Context context, int delay) {
        if (null == imm) {
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        Disposable disposable = Observable.timer(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });
    }

    /**
     * 关闭软键盘
     *
     * @param activity 当前activity
     */
    public static void closeKeyBoard(Activity activity) {

        /**隐藏软键盘**/
        final View view = activity.getWindow().peekDecorView();
        if (view != null) {
            final InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);

            Disposable disposable = Observable.timer(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            view.clearFocus();
                            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    });
        }
    }


    /**
     * 关闭软键盘
     *
     * @param context 当前上下文
     */
    public static void closeKeyBoard(Context context) {

        if (context instanceof Activity) {

            /**隐藏软键盘**/
            final View view = ((Activity) context).getWindow().peekDecorView();
            if (view != null) {
                final InputMethodManager inputmanger = (InputMethodManager) ((Activity) context).getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Disposable disposable = Observable.timer(500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                view.clearFocus();
                                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        });
            }
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param view    EditText
     * @param context 上下文
     */
    public static void hideSoft(final EditText view, final Context context) {
        if (null == imm)
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        Disposable disposable = Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
//                        Run.onUiSync(new Action() {
//                            @Override
//                            public void call() {
//                                if (view != null) {
//                                    view.clearFocus();
//                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                                }
//                            }
//                        });
                    }
                });

    }

    /**
     * 关闭软键盘 不需要延时
     *
     * @param context 当前上下文
     */
    public static void hideKeyBoard(Context context) {
        if (context instanceof Activity) {
            ((Activity) context).getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    /**
     * 将字符串转换成md5
     *
     * @param paramString 需要转换的string值
     * @return 转换成的MD5
     */
    public static String md5(String paramString) {
        String returnStr;
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramString.getBytes());
            returnStr = byteToHexString(localMessageDigest.digest());
            return returnStr;
        } catch (Exception e) {
            return paramString;
        }
    }

    /**
     * 将指定byte数组转换成16进制字符串
     *
     * @param bytes btye[]数组
     * @return String
     */
    public static String byteToHexString(byte[] bytes) {
        String str;
        StringBuilder hexString = new StringBuilder();
        for (byte aB : bytes) {
            String hex = Integer.toHexString(aB & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        str = hexString.toString();
        return str;
    }


    /**
     * 退出应用程序
     *
     * @param context 上下文
     */
    public static void AppExit(Context context) {
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
        }
    }


    /**
     * 获取格式化后的金额字符串
     *
     * @param money 金额
     * @return 格式化后的金额字符串
     */
    public static String getFormatMoney(double money) {
        NumberFormat nf;
        int tmp = (int) money;
        if (money - tmp != 0) {
            nf = new DecimalFormat("##0.00");
            String str = nf.format(money);
            str = str.replaceAll("0+?$", "");//去掉后面无用的零
            return str;
        } else {
            nf = new DecimalFormat("##0");
            return nf.format(money);
        }
    }


    /**
     * 检测Sdcard是否存在
     *
     * @return false  true
     */
    public static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    /**
     * 获取到当年
     *
     * @param calendar Calendar
     * @return 当年
     */
    public static int getCurrentYear(Calendar calendar) {

        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取到当月
     *
     * @param calendar Calendar
     * @return 当月
     */
    public static int getCurrentMonth(Calendar calendar) {

        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取到当天
     *
     * @param calendar Calendar
     * @return 当天
     */
    public static int getCurrrentDay(Calendar calendar) {

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Point getScreenHeightAndWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;

    }

    /**
     * 字符串转换成日期
     *
     * @param str String
     * @return date
     */
    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    /**
     * 判断应用前后台
     *
     * @param context 上下文
     */

    public static boolean isBackground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            assert activityManager != null;
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(context.getPackageName())) {
                    if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 获取控件宽度
     */
    public static float getViewWidth(View view, boolean type) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        if (type)
            return view.getMeasuredWidth();
        else
            return view.getMeasuredHeight();

    }


    public static ArrayList<Calendar> getCurrentMonthCalendar(Calendar c, int offset) {
        ArrayList<Calendar> calendars = new ArrayList<>();
        Calendar currentMonth = (Calendar) c.clone();
        currentMonth.add(Calendar.MONTH, offset);


        Calendar cal = (Calendar) c.clone();
        cal.add(Calendar.MONTH, offset);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int off = cal.getFirstDayOfWeek() - firstDayOfWeek;
        if (off > 0) {
            off -= 7;
        }
        cal.add(Calendar.DATE, off);

        while ((cal.get(MONTH) < currentMonth.get(Calendar.MONTH) + 1 || cal.get(YEAR) < currentMonth.get(Calendar.YEAR)) //
                && cal.get(YEAR) <= currentMonth.get(Calendar.YEAR)) {
            for (int i = 0; i < 7; i++) {
                calendars.add((Calendar) cal.clone());
                cal.add(Calendar.DATE, 1);
                Log.i("aaa", cal.get(Calendar.MONTH) + "--" + cal.get(Calendar.DAY_OF_MONTH));
            }
        }
        return calendars;
    }


    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {//闰年
                    timeDistance += 366;
                } else {//不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {
//            Logger.i("判断day2 - day1 : " + (day2 - day1));
            return day2 - day1;
        }
    }

    /**
     * 转化不同大小颜色字体
     *
     * @param start         开始位置
     * @param end           结束位置
     * @param size          大小
     * @param color         颜色
     * @param isChangeSize  是否改变大小
     * @param isChanegColor 改变颜色
     * @param content       内容
     * @param context       上下文
     * @return SpannableString
     */
    public static SpannableString setTextStyle(int start, int end, int size, int color, boolean isChangeSize, boolean isChanegColor, String content, Context context) {
        SpannableString ss = new SpannableString(content);
//        if (isChangeSize) {
//            ss.setSpan(new AbsoluteSizeSpan(DisplayUtils.dp2px(context, size)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        }
//        if (isChanegColor) {
//            ss.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        }
        return ss;
    }


    /**
     * 转化不同大小颜色字体
     *
     * @param context 上下文
     * @return SpannableString
     */
    public static SpannableString setTextStyle(int startSize, int endSize, String startText, String endText, Context context) {
        SpannableString ss = new SpannableString(startText + endText);
        int start = 0;
        int middle = startText.length();
        int end = middle + endText.length();
//        ss.setSpan(new AbsoluteSizeSpan(startSize), start, middle -1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(endSize), middle, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return ss;
    }

    /**
     * 转化不同大小颜色字体
     *
     * @return SpannableString
     */
    public static SpannableStringBuilder setTextStyle(String startText, String middleText, int middleColor, Context context) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(startText).append(" ").append(middleText);
        int index = startText.length();
        int end = spannableStringBuilder.toString().length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(middleColor), index, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }


    /**
     * 转化不同大小颜色字体
     *
     * @return SpannableString
     */
    public static SpannableStringBuilder setTextStyle(String startText, String middleText, String endText, int middleColor, Context context) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(startText).append(" ").append(middleText).append(" ").append(endText);
        int index = startText.length();
        int end = startText.length() + middleText.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(middleColor), index, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * 转化不同大小颜色字体
     *
     * @return SpannableString
     */
    public static SpannableStringBuilder setTextStyle(String startText,
                                                      int startColor, String endText, int endColor) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(startText).append(endText);
        int index = startText.length();
        int end = index + endText.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(startColor), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(endColor), index, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * 转化不同大小颜色字体
     *
     * @return SpannableString
     */
    public static SpannableStringBuilder setTextStyle(String startText, int startColor,
                                                      String secondText, int secondColor,
                                                      String threeText, int threeColor,
                                                      String endText, int endColor) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder
                .append(startText)
                .append(secondText)
                .append(threeText)
                .append(endText);
        int index = startText.length();
        int secondIndex = index + secondText.length();
        int threeIndex = secondIndex + threeText.length();
        int end = threeIndex + endText.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(startColor), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(secondColor), index, secondIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(threeColor), secondIndex, threeIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(endColor), threeIndex, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }


    /**
     * 转化不同大小颜色字体
     *
     * @return SpannableString
     */
    public static SpannableStringBuilder setTextStyle(String startText, String endText, int startColor, int endColor, Context context) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(startText).append(endText);
        int index = startText.length();
        int end = index + endText.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(startColor), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(endColor), index, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }


    /**
     * @param firstAnnualized  年化范围  低范围
     * @param contentTextSize  内容的字体大小
     * @param annualizedType   % ~
     * @param secondAnnualized 年化范围  高范围
     * @param tipTextSize      % ~ 字体大小
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder setTextStyle(String firstAnnualized,
                                                      int contentTextSize,
                                                      String annualizedType,
                                                      String secondAnnualized,
                                                      int tipTextSize) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        spannableStringBuilder
                .append(firstAnnualized)
                .append(" ")
                .append(annualizedType)
                .append(" ")
                .append(secondAnnualized)
                .append(" %")
        ;

        int firstStartIndex = 0;
        int firstEndIndex = firstAnnualized.length();

        int secondStartIndex = firstEndIndex + 1;
        int secondEndIndex = secondStartIndex + annualizedType.length();

        int threeStartIndex = secondEndIndex + 1;
        int threeEndIndex = threeStartIndex + secondAnnualized.length();

        int fourStartIndex = threeEndIndex + 1;
        int fourEndIndex = spannableStringBuilder.toString().length();

        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(contentTextSize), firstStartIndex, firstEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(tipTextSize), secondStartIndex, secondEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(contentTextSize), threeStartIndex, threeEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(tipTextSize), fourStartIndex, fourEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }


    /**
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder setTxtStyle(String firstContent,
                                                     int firstSize,
                                                     String secondContent,
                                                     int secondSize) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        spannableStringBuilder
                .append(firstContent)
                .append(secondContent)
        ;

        int firstStartIndex = 0;
        int firstEndIndex = firstContent.length();

        int secondStartIndex = firstEndIndex + 1;
        int secondEndIndex = firstEndIndex + secondContent.length();

        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(firstSize), firstStartIndex, firstEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(secondSize), secondStartIndex, secondEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }

    /**
     * 给字符串加*号
     *
     * @param code  String
     * @param start 开始位置
     * @param end   结束位置
     * @return String
     */
    public static String encryptCode(String code, int start, int end) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            if (i >= start && i < end) {
                stringBuilder.append("*");
            } else {
                stringBuilder.append(code.charAt(i));
            }

        }
        return stringBuilder.toString();
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context          上下文
     * @param packageName：应用包名
     * @return false  true
     */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);

        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                if (packageInfos.get(i).packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * 给一个view设置长宽
     *
     * @param view   View
     * @param width  宽
     * @param height 高
     */
    public static void setViewSize(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (null == layoutParams)
            return;
        if (width > 0) {
            layoutParams.width = width;
        }
        if (height > 0) {
            layoutParams.height = height;
        }

        view.setLayoutParams(layoutParams);
    }


    /**
     * 获取当前时间
     *
     * @param format 格式化
     */
    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }


    /**
     * 解决水平滑动布局和scrollview的滑动冲突
     *
     * @param view       View
     * @param scrollView ScrollView
     */
    public static void solveScrollConflict(View view, final ScrollView scrollView) {
        view.setOnTouchListener(new View.OnTouchListener() {
            float ratio = 1.2f; //水平和竖直方向滑动的灵敏度,偏大是水平方向灵敏
            float x0 = 0f;
            float y0 = 0f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x0 = event.getX();
                        y0 = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = Math.abs(event.getX() - x0);
                        float dy = Math.abs(event.getY() - y0);
                        x0 = event.getX();
                        y0 = event.getY();
                        scrollView.requestDisallowInterceptTouchEvent(dx * ratio > dy);
                        break;
                }
                return false;
            }
        });
    }

    public static int binaryString(int userType) {
        String binaryString = Integer.toBinaryString(userType);
        char at = binaryString.charAt(binaryString.length() - 1);
        try {
            return Integer.parseInt(String.valueOf(at));
        } catch (Exception e) {
            return -1;
        }
    }

    public static void copy(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);
    }


    public static boolean bitOperation(int userType, int bit) {
        return (userType & (1 << bit)) > 0;
    }

    public static boolean isDebug(Context context) {

//        String asString = ACache.get(context).getAsString(BaseCoinConstant.Constance.IS_DEBUG);
//        if (asString != null) {
//            if (asString.equals(BaseCoinConstant.AppHostType.DEBUG.getMsg())) {
//                return true;
//            } else if (asString.equals(BaseCoinConstant.AppHostType.RELEASE.getMsg())) {
//                return false;
//            }
//        }
        return false;
    }

    private static String mUserAgent = "";

    public static String getAndroidUserAgent(Context context) {
        if (TextUtils.isEmpty(mUserAgent)) {
            String appVersion = VersionUtils.getVersion(context);
            String userAgent = "Bingbon/";
            String platForm = " Android; " + Build.VERSION.RELEASE;
            StringBuilder userAgentSb = new StringBuilder();
            userAgentSb.append(userAgent).append(appVersion).append(platForm);
            userAgentSb.append("Contract/")
                    .append(VersionUtils.getVersion(BaseApplication.getApp()));
            mUserAgent = !TextUtils.isEmpty(userAgentSb.toString()) ? userAgentSb.toString() : "Bingbon/Android";
            return mUserAgent;
        }
        return mUserAgent;
    }

    public static boolean isOfficial() {
        return getInstallChannel(BaseApplication.getApp()).equals("officialAPK");
    }

    public static boolean isGooglePlay() {
        return getInstallChannel(BaseApplication.getApp()).equals("googleplay");
    }


    public static String getInstallChannel(Context context) {
        String installChannel = "";
        try {
            if (TextUtils.isEmpty(installChannel)) {
                installChannel = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA)
                        .metaData.getString("BUGLY_APP_CHANNEL");
            }
            if (TextUtils.isEmpty(installChannel)) {
                installChannel = "officialAPK";
            }
        } catch (Exception e) {
            installChannel = "officialAPK";
        }
        return installChannel;
    }

    private static String isGrayStr = "";

    public static Boolean getIsGrayTest(Context context) {

        if (!TextUtils.isEmpty(isGrayStr)) {
            return "gray".equalsIgnoreCase(isGrayStr);
        }
        try {
            isGrayStr = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA)
                    .metaData.getString("IS_GRAY_TEST");
            if (TextUtils.isEmpty(isGrayStr)) {
                isGrayStr = "release";
            }
        } catch (Exception e) {
            isGrayStr = "release";
        }
        return "gray".equalsIgnoreCase(isGrayStr);
    }

    /**
     * 保存二维码到本地
     *
     * @param bitmap bitmap
     */
    public static void saveImage(final Context context, final Bitmap bitmap, boolean showToast) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (context instanceof AppCompatActivity) {
                RxPermissions rxPermissions = new RxPermissions((AppCompatActivity) context);
                Disposable disposable = rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    if (bitmap != null) {
                                        String fileName = saveImageToGallery(context, bitmap);
                                        if (showToast) {
                                            if (!TextUtils.isEmpty(fileName)) {
//                                                SafeToast.makeImageText(context.getString(R.string.save_success));
                                            } else {
//                                                SafeToast.makeText(context.getString(R.string.save_error));
                                            }
                                        }
                                    }
                                } else {
//                                    SafeToast.makeText(context.getString(R.string.please_grant_permission));
                                }
                            }
                        });
            }
        } else {
            if (bitmap != null) {
                String fileName = saveImageToGallery(context, bitmap);
                if (showToast) {
                    if (!TextUtils.isEmpty(fileName)) {
//                        SafeToast.makeImageText(context.getString(R.string.save_success));
                    } else {
//                        SafeToast.makeText(context.getString(R.string.save_error));
                    }
                }
            }
        }


    }


    /**
     * 保存二维码到本地
     *
     * @param bitmap bitmap
     */
    private static void saveImage(final Context context, final Bitmap bitmap) {
        saveImage(context, bitmap, true);
    }

    public interface SaveImgListener {
        void onSuccess(String str);
    }

    /**
     * 保存二维码到本地
     *
     * @param bitmap bitmap
     */
    public static void saveImage(final Context context, final Bitmap bitmap, final SaveImgListener listener) {
        saveImage(context, bitmap, true, listener);
    }

    /**
     * 保存二维码到本地
     *
     * @param bitmap bitmap
     */
    public static void saveImage(final Context context, final Bitmap bitmap, boolean showToast, final SaveImgListener listener) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (context instanceof AppCompatActivity) {
                RxPermissions rxPermissions = new RxPermissions((AppCompatActivity) context);
                Disposable disposable = rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    if (bitmap != null) {
                                        String fileName = saveImageToGalleryTemp(context, bitmap);
                                        if (!TextUtils.isEmpty(fileName)) {
                                            if (listener != null) {
                                                listener.onSuccess(fileName);
                                            }
                                        }
                                    }
                                } else {
                                    if (showToast) {
//                                        SafeToast.makeText(context.getString(R.string.please_grant_permission));
                                    }
                                }
                            }
                        });
            }
        } else {
            if (bitmap != null) {
                String fileName = saveImageToGalleryTemp(context, bitmap);
                if (showToast) {
                    if (!TextUtils.isEmpty(fileName)) {
//                        SafeToast.makeText(context.getString(R.string.save_success));
                    } else {
//                        SafeToast.makeText(context.getString(R.string.save_error));
                    }
                }
            }
        }


    }


    //保存文件到指定路径
    private static String saveImageToGalleryTemp(Context context, Bitmap bmp) {
        // 首先保存图片

        String storePath =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath()
                        + File.separator + "bingxtemp";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return storePath + File.separator + fileName;
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断Bitmap是否都是一个颜色
     *
     * @return
     */
    public static boolean isBlank(View view) {

        if (view == null) return true;

        Bitmap bitmap = getBitmapFromView(view);

        if (bitmap == null) {
            return true;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > 0 && height > 0) {
            int originPix = bitmap.getPixel(0, 0);
            int[] target = new int[width];
            Arrays.fill(target, originPix);
            int[] source = new int[width];
            boolean isWhiteScreen = true;
            for (int col = 0; col < height; col++) {
                bitmap.getPixels(source, 0, width, 0, col, width, 1);
                if (!Arrays.equals(target, source)) {
                    isWhiteScreen = false;
                    break;
                }
            }
            return isWhiteScreen;
        }
        return false;
    }

    /**
     * 从View获取转换到的Bitmap
     *
     * @param view
     * @return
     */
    private static Bitmap getBitmapFromView(View view) {

        if (view == null) return null;
        if (view.getWidth() == 0 || view.getHeight() == 0) return null;

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (Build.VERSION.SDK_INT >= 11) {
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
            view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());
        } else {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        view.draw(canvas);
        return bitmap;
    }


    // 用于保存bitmap到相册
    public static void saveBitmap(Context context, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            SaveUtils.Fun.INSTANCE.saveBitmap2Galley(context, bitmap, String.valueOf(System.currentTimeMillis()), true);
        } else {
            saveImage(context, bitmap);
        }
    }

    //保存文件到指定路径
    public static String saveImageToGallery(Context context, Bitmap bmp) {

        // 首先保存图片
        String storePath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator + "bingx";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return storePath + File.separator + fileName;
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getStringDigits(String price) {
        if (TextUtils.isEmpty(price)) {
            return 4;
        }
        String[] num = price.split("\\.");
        if (num.length == 2) {
            return num[1].length();
        } else {
            return 4;
        }
    }

    public static int getNumberDecimalDigits(BigDecimal number) {
        String moneyStr = String.valueOf(number);
        String[] num = moneyStr.split("\\.");
        if (num.length == 2) {
            return num[1].length();
        } else {
            return 2;
        }
    }


    //数字
    public static final String REG_NUMBER = ".*\\d+.*";
    //大写字母
//    public static final String REG_UPPERCASE = ".*[A-Z]+.*";
    //小写字母
    public static final String REG_LOWERCASE = ".*[a-z]+.*";

    public static boolean checkPasswordRule(String password) {
        //密码为空及长度大于8位小于30位判断
        if (password == null || password.length() < 8 || password.length() > 30) return false;

        int i = 0;

        if (password.matches(REG_NUMBER)) i++;
        if (password.matches(REG_LOWERCASE)) i++;
//        if (password.matches(REG_UPPERCASE)) i++;

        if (i < 2) return false;
        return true;
    }


    public static boolean check6Pwd(String password) {
        //数字不是连续的
        String regres = "^(?:(\\d)(?!((?<=9)8|(?<=8)7|(?<=7)6|(?<=6)5|(?<=5)4|(?<=4)3|(?<=3)2|(?<=2)1|(?<=1)0){5})(?!\1{5})(?!((?<=0)1|(?<=1)2|(?<=2)3|(?<=3)4|(?<=4)5|(?<=5)6|(?<=6)7|(?<=7)8|(?<=8)9){5})){6}$";
        //数字不是重复的
        String reg = "^(?=.*\\d+)(?!.*?([\\d])\\1{5})[\\d]{6}$";
        if (!Pattern.matches(regres, password) || !Pattern.matches(reg, password)) {
            return false;
        } else {
            return true;
        }
    }


    public static String regexAccount(String account) {
        if (!TextUtils.isEmpty(account) && account.length() > 4) {
            StringBuilder stringBuilder = new StringBuilder();
            int accountLength = account.length();
            String firstLength = account.substring(0, 3);
            stringBuilder.append(firstLength)
                    .append("****")
                    .append(account.substring(account.length() - 4));
            return stringBuilder.toString();
        } else {
            return "";
        }
    }


    // 4个不能连续
    private static String regs = "0123456789_9876543210";

    //4个数字不是重复的
    private static String reg = "^(?=.*\\d+)(?!.*?([\\d])\\1{3})[\\d]{6}$";


    // 检测资金密码强度
    public static boolean checkAssetStrength(String pwd) {
        if (testPwd(pwd)) {
            if (pwdThreeRepeat(pwd)) {
                return true;
            }
        }
        return false;
    }

    public static boolean pwdThreeRepeat(String pwd) {

        if (pwd.length() != 6) {
            return false;
        }
        String str1 = pwd.substring(0, 4);
        String str2 = pwd.substring(1, 5);
        String str3 = pwd.substring(2, 6);

        if (regs.indexOf(str1) > 0 || regs.indexOf(str2) > 0 || regs.indexOf(str3) > 0) {
            return false;
        }
        return true;
    }

    public static Boolean testPwd(String pwd) {
        if (!Pattern.matches(reg, pwd)) {
            return false;
        }
        return true;
    }


    public static String regexEmail(String account) {
        if (!TextUtils.isEmpty(account) && account.length() > 4) {
            StringBuilder stringBuilder = new StringBuilder();
            String firstLength = account.substring(0, 2);
            String[] split = account.split("@");
            String endStr = "";
            if (split.length == 2) {
                endStr = split[1];
            }
            stringBuilder.append(firstLength)
                    .append("****@")
                    .append(endStr);
            return stringBuilder.toString();
        } else {
            return "";
        }
    }

    public static boolean isEmail(String account) {
        if (TextUtils.isEmpty(account)) {
            return false;
        }
        if (account.contains("@")) {
            return true;
        }

        return false;
    }

    public static boolean hasOpenedDialogs(FragmentActivity activity) {
        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof DialogFragment) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isFirstInstall(Context context) {
        return getPackageFirstInstallTime(context) == getPackageLastUpdateTime(context);
    }

    private static long getPackageFirstInstallTime(Context context) {
        String name = context.getPackageName();
        long time = 0;
        try {
            time = context.getPackageManager().getPackageInfo(name, 0).firstInstallTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    private static long getPackageLastUpdateTime(Context context) {
        String name = context.getPackageName();
        long time = 0;
        try {
            time = context.getPackageManager().getPackageInfo(name, 0).lastUpdateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static boolean isEmulator(Context context) {
        Context baseContext = context.getApplicationContext();
        String url = "tel:" + "123456";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_DIAL);
        // 是否可以处理跳转到拨号的 Intent
        boolean canResolveIntent = intent.resolveActivity(baseContext.getPackageManager()) != null;
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.SERIAL.equalsIgnoreCase("unknown")
                || Build.SERIAL.equalsIgnoreCase("android")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || ((TelephonyManager) baseContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName().toLowerCase().equals("android")
                || !canResolveIntent;
    }

    // 浅拷贝
    //list A浅拷贝给list B，由于进行的是浅拷贝，所以直接将A的内容复制给了B，java中相同内容的数组指向同一地址，
    // 即进行浅拷贝后A与B指向同一地址。造成的后果就是，改变B的同时也会改变A，因为改变B就是改变B所指向地址的内容，由于A也指向同一地址，所以A与B一起改变。

    /**
     * 深拷贝
     * 用于ListB改变之后不要影响ListA的值
     * 深拷贝就是将A复制给B的同时，给B创建新的地址，再将地址A的内容传递到地址B。
     * ListA与ListB内容一致，但是由于所指向的地址不同，所以改变相互不受影响。
     *
     * @param src
     * @return
     */
    public static List deepCopy(List src) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteOut);) {
            outputStream.writeObject(src);
            try (ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
                 ObjectInputStream inputStream = new ObjectInputStream(byteIn);
            ) {
                return (List) inputStream.readObject();
            }
        } catch (Exception e) {
            return src;
        } finally {

        }
    }

    //获取图片缓存路径uri
    public static Uri getImageTempUri(Context context, Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        String storePath =
                context.getCacheDir().getAbsolutePath() + File.separator + "bingx";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            Uri uri = Uri.fromFile(file);
            if (isSuccess) {
                return uri;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // base64转图片 bitmap  注意要去掉 data:image/png;base64,
    public static Bitmap base64ToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String getPPPrecision(int priceDigitalNum) {
        StringBuilder sb = new StringBuilder();
        if (priceDigitalNum > 1) {
            sb.append("0.");
            for (int i = 0; i < priceDigitalNum; i++) {
                if (i == priceDigitalNum - 1) {
                    sb.append("1");
                } else {
                    sb.append("0");
                }
            }
        } else {
            sb.append("0.1");
        }
        return sb.toString();
    }


    private static Map<String, String> webHeaders = new HashMap<>();

    // 清除下 如果换了语言
    public static void clearWebHeaders() {
        if (webHeaders.isEmpty()) {
            return;
        }
        webHeaders.clear();
    }

    public static Map<String, String> getWebHeaders() {
        if (webHeaders.isEmpty()) {
            if (LanguageUtils.getInstance().isEnglishType()) {
                webHeaders.put("lang", "en-us");
            } else {
                webHeaders.put("lang", LanguageUtils.getInstance().getCurrentLanguageTip());
            }
        }
        return webHeaders;
    }

    /**
     * 通过浏览器下载APK更新安装
     *
     * @param context    上下文
     * @param httpUrlApk APK下载地址
     */
    public static void installAPKWithBrower(Context context, String httpUrlApk) {
        Uri uri = Uri.parse(httpUrlApk);
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(viewIntent);
    }

    // support.bingx.com  要拿到bingx.com
    // bingx.com  返回bingx.com
    public static String getFirstDomain(String url) {
        String domain = "";
        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
            try {
                String host = Uri.parse(url).getHost();
                if (!TextUtils.isEmpty(host) && host.contains(".")) {
                    String b = ".";
                    int count = (host.length() - host.replace(b, "").length()) / b.length();
                    if (count > 1) {
                        // support.bingx.com  要拿到bingx.com
                        String substring = host.substring(host.indexOf("."));
                        if (substring.startsWith(".")) {
                            domain = substring.substring(1);
                        } else {
                            domain = substring;
                        }
                    } else {
                        domain = host;
                    }
                }
            } catch (Exception e) {

            }
        }
        return domain;
    }

    /**
     * 检测系统是否已经设置代理，请参考HttpDNS API文档。
     */
    public static boolean detectIfProxyExist(Context ctx) {
        boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyHost;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyHost = System.getProperty("http.proxyHost");
            String port = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt(port != null ? port : "-1");
        } else {
            proxyHost = android.net.Proxy.getHost(ctx);
            proxyPort = android.net.Proxy.getPort(ctx);
        }
        return proxyHost != null && proxyPort != -1;
    }

    // 是否是数字
    public static boolean isDigital(String str) {
        try {
            new BigDecimal((str));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static int parseStrForInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }

    public static double parseStrForDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0d;
        }
    }

    public static double parseStrForDouble(int precision, String str) {
        try {
            int index = str.indexOf(".");
            if(index == -1) {
                return Double.parseDouble(str);
            } else {
                return Double.parseDouble(str.substring(0,Math.min(index + precision + 1, str.length())));
            }
        } catch (Exception e) {
            return 0d;
        }
    }

}