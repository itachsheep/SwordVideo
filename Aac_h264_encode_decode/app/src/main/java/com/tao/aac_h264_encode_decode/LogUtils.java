/**
 * @ClassName: LogUtils
 * @Description:
 * @author taowei
 * @version V1.0
 * @Date
 */

package com.tao.aac_h264_encode_decode;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "AacH264.";

    public static void d(String tag, String msg) {
        Log.d(TAG + tag,msg);
    }
}
