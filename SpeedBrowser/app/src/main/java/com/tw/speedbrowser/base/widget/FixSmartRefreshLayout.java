package com.tw.speedbrowser.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * 修复第三方控件问题：https://bugly.qq.com/v2/crash-reporting/crashes/e0a9003bc6/2309218?pid=1
 *后续所有控下拉刷新使用这个，修复的已在2.48.0的版本中得到验证
 */
public class FixSmartRefreshLayout extends SmartRefreshLayout {
    public FixSmartRefreshLayout(Context context) {
        super(context);
    }

    public FixSmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
