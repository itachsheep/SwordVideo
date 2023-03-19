package com.tw.speedbrowser.base.widget;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.tw.speedbrowser.R;
import com.tw.speedbrowser.base.utils.ResUtils;


public class KeyboardStatusDetector {
    private static final int SOFT_KEY_BOARD_MIN_HEIGHT = 430;
    private KeyboardVisibilityListener mVisibilityListener;

    boolean keyboardVisible = false;

    private int keyboardHeight = 0;

    public void registerFragment(Fragment f) {
        if (f.getView() != null) {
            registerView(f.getView());
        }
    }

    public void registerActivity(Activity a) {
        registerView(a.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    public KeyboardStatusDetector registerView(final View v) {
        v.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            v.getWindowVisibleDisplayFrame(r);
            int heightDiff = v.getRootView().getHeight() - (r.bottom - r.top);
            if (heightDiff > SOFT_KEY_BOARD_MIN_HEIGHT) { // if more than 100 pixels, its probably a keyboard...
                setKeyboardHeight(heightDiff);
                if (!keyboardVisible) {
                    keyboardVisible = true;
                    if (mVisibilityListener != null) {
                        mVisibilityListener.onVisibilityChanged(true);
                    }
                }
            } else {
                if (keyboardVisible) {
                    keyboardVisible = false;
                    if (mVisibilityListener != null) {
                        mVisibilityListener.onVisibilityChanged(false);
                    }
                }
            }
        });

        return this;
    }

    public KeyboardStatusDetector setmVisibilityListener(KeyboardVisibilityListener listener) {
        mVisibilityListener = listener;
        return this;
    }

    public interface KeyboardVisibilityListener {
        void onVisibilityChanged(boolean keyboardVisible);
    }

    public int getKeyboardHeight() {
        return keyboardHeight - ResUtils.getDimensionPixelOffset(R.dimen.len_95);
    }

    public void setKeyboardHeight(int keyboardHeight) {
        this.keyboardHeight = keyboardHeight;
    }
}
