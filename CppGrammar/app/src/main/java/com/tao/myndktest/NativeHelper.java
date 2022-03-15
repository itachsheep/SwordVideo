/**
 * @ClassName:
 * @Description:
 * @author taowei
 * @version V1.0
 * @Date
 */

package com.tao.myndktest;

public class NativeHelper {
    public native void testTemplate();

    /**
     *  c 和 c++ 互相调用
     */
    public native void testC();

    public native void test_pthread();
}
