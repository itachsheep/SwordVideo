package com.tw.speedbrowser.base

import io.reactivex.disposables.CompositeDisposable

/**
 * 懒加载fragments的基类（后续所有的fragment都要继承这个类）
 * 使用规则：
 *      1.当前fragment如果需要懒加载重写  isLazyLoadData 返回true，默认false
 *      2。如果当前需要注册eventbus，重写isEventBusRun，返回true，默认false
 *      3。如果需要监听当前fragment的可见状态，可重写onVisibleToUserChanged这个方法
 * 注意事项：
 *      1.当viewpager多层嵌套或者fragment多层嵌套时，一定要保证所有的fragment都是继承这个基类，否则懒加载会失效！！！
 */
abstract class BaseLazyLoadFragment : BaseNewFragment() {

    protected var isFirstLoadData = true

    companion object {
        const val REQUEST_WHEN_SCROLL_DELAY = 550L
    }

    //这个Disposable针对的在当前所有的Disposable
    protected val mCompositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        isFirstLoadData = true
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.clear()
        }
    }

    override fun isLazyLoadData(): Boolean {
        return false
    }

    override fun isEventBusRun(): Boolean {
        return false
    }

    override fun onVisibleToUserChanged(isVisibleToUser: Boolean) {
        super.onVisibleToUserChanged(isVisibleToUser)
        if (isLazyLoadData && isFirstLoadData && isVisibleToUser) {
            isFirstLoadData = false
            initData()
        }
    }

    //    protected IMSViewModel getIMSViewModel() {
    //        if (mIMSViewModel == null) {
    //            mIMSViewModel = IMSKit.INSTANCE.getImsViewModel();
    //        }
    //
    //        return mIMSViewModel;
    //    }


    override fun onBackPressed(): Boolean {
        return false
    }
}