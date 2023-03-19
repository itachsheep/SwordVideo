package com.tw.speedbrowser.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.freddy.event.CEventCenter;
import com.freddy.event.I_CEventListener;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.tw.speedbrowser.base.widget.LoadingDialog;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 全新的基类
 */
public abstract class BaseNewFragment extends RxFragment implements FragmentUserVisibleController.UserVisibleCallback, I_CEventListener {
    private static final String TAG = "BaseNewFragment";

    private View mRootView;

    protected Context instance;

    public boolean mIsVisible = false;

    public boolean isReleased = true;

    protected boolean hasPreLoaded = false;

    protected long mOnCreateTime = 0L;
    protected boolean mFirstCreateForLog = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initArgus(getArguments());
        instance = context;
    }

    private FragmentUserVisibleController userVisibleController;

    public BaseNewFragment() {
        userVisibleController = new FragmentUserVisibleController(this, this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //BxLog.INSTANCE.d(TAG,"onActivityCreated: " + (this.getClass().getSimpleName()));
        userVisibleController.activityCreated();
    }

    @Override
    public void onResume() {
        super.onResume();
        //BxLog.INSTANCE.d(TAG,"onResume: " + (this.getClass().getSimpleName()));
        mIsVisible = true;
        userVisibleController.resume();
//        reportPageView(getReportPageViewExtraParams());
    }

    protected JSONObject getReportPageViewExtraParams() {
        return null;
    }

    private ArrayList<String> pathList = new ArrayList<>(20);
    private String getPageUrl() {
        pathList.clear();
        pathList.add(this.getClass().getSimpleName());
        Fragment pFrg = getParentFragment();
        while (pFrg != null) {
            pathList.add(pFrg.getClass().getSimpleName());
            pFrg = pFrg.getParentFragment();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = pathList.size() - 1; i >= 0; i--) {
            if(i == 0) {
                sb.append(pathList.get(i));
            } else {
                sb.append(pathList.get(i)).append("_");
            }
        }
        return sb.toString();
    }

//    protected void reportPageView(JSONObject extra) {
//        AutoBuryLog autoBuryLog = new AutoBuryLog();
//        autoBuryLog.setPage_url(getPageUrl());
//        if (extra != null) {
//            autoBuryLog.setUrl_param(extra);
//        }
//        AppUploadLog.INSTANCE.sysUploadLog(LogId.AutoBuryId.EVENT_OVERALL_ALL_PAGEVIEW,
//                GsonUtils.getInstance().mGson.toJson(autoBuryLog));
//    }

    @Override
    public void onPause() {
        super.onPause();
        mIsVisible = false;
        userVisibleController.pause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        userVisibleController.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void setWaitingShowToUser(boolean waitingShowToUser) {
        userVisibleController.setWaitingShowToUser(waitingShowToUser);
    }

    @Override
    public boolean isWaitingShowToUser() {
        return userVisibleController.isWaitingShowToUser();
    }

    @Override
    public boolean isVisibleToUser() {
        return userVisibleController.isVisibleToUser();
    }

    @Override
    public void callSuperSetUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onVisibleToUserChanged(boolean isVisibleToUser) {
    }


    public void onLoading(boolean loading) {
        ((Activity) instance).runOnUiThread(() -> {
            try {
                if (loading) showCusLoading();
                else hideCusLoading();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //BxLog.INSTANCE.d(TAG,"onStart: " + (this.getClass().getSimpleName()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //BxLog.INSTANCE.d(TAG,"onViewCreated: "  + (this.getClass().getSimpleName()));
        isReleased = false;
        initView(mRootView);
        initViewModel();
//        if (isEventBusRun() && !EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
        if (!isLazyLoadData()) {
            initData();
        }
        initListener();
        String[] eventCenterTopics = getEventCenterTopics();
        if (eventCenterTopics != null && eventCenterTopics.length > 0) {
            CEventCenter.registerEventListener(this, eventCenterTopics);
        }
//        OOMHelper.INSTANCE.put(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //BxLog.INSTANCE.d(TAG,"onCreateView: "  + (this.getClass().getSimpleName()));
        if (mRootView == null) {
            mRootView = LayoutInflater.from(instance).inflate(setLayoutId(), container, false);
        } else {
            if (mRootView.getParent() != null) {
                //把当前root从其父控件中移除
                ((ViewGroup) mRootView.getParent()).removeView(mRootView);
            }
        }
        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //BxLog.INSTANCE.d(TAG,"onAttach: "  + (this.getClass().getSimpleName()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirstCreateForLog = true;
        //BxLog.INSTANCE.d(TAG,"onCreate: " + (this.getClass().getSimpleName()));
        mOnCreateTime = System.currentTimeMillis();

    }

    /**
     * 控件监听
     */
    protected abstract void initListener();

    /**
     * 设置布局
     *
     * @return layoutId
     */
    protected abstract @LayoutRes
    int setLayoutId();

    /**
     * 传递的数据 Bundle
     *
     * @param arguments Bundle¬
     */
    public void initArgus(Bundle arguments) {

    }

    /**
     * 加载数据
     *
     * @return initData
     */
    protected void initData() {
    }


    /**
     * 是否启动EventBus，若启用但域中没有@Subsrcibe修饰的方法的话会报错。
     * 若不想使用，重写它并返回false即可。
     *
     * @return 默认为true ,true则为启用
     */
    protected abstract boolean isEventBusRun();


    /**
     * 是否启动懒加载。
     * 若不想使用，重写它并返回false即可。
     *
     * @return 默认为true ,true则为启用
     */
    protected abstract boolean isLazyLoadData();


    /**
     * 初始化Viewmodel方法
     */
    protected abstract void initViewModel();


    private android.app.Dialog mDialog;

    public void showCusLoading() {
        ((Activity) instance).runOnUiThread(() -> {
            if (mDialog == null) {
                mDialog = new LoadingDialog(instance);
                mDialog.setCancelable(true); // true设置返回取消
                mDialog.setCanceledOnTouchOutside(true); // 设置点按取消loading
            }
            try {
                if (!mDialog.isShowing()) {
                    mDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void hideCusLoading() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            });
        }
    }


    /**
     * 初始化view
     *
     * @param view mRootView
     */
    public abstract void initView(View view);

    @Override
    public void onDestroyView() {
//        if (isEventBusRun() && EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
        String[] eventCenterTopics = getEventCenterTopics();
        if (eventCenterTopics != null && eventCenterTopics.length > 0) {
            CEventCenter.unregisterEventListener(this, eventCenterTopics);
        }
        super.onDestroyView();
        isReleased = true;
    }

    /**
     * fragment拦截返回键返回true，不拦截返回false
     */
    public abstract boolean onBackPressed();

    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        if (isReleased) return;
        processEvent(topic, msgCode, resultCode, obj);
    }

    protected void processEvent(String topic, int msgCode, int resultCode, Object event) {
    }

    protected String[] getEventCenterTopics() {
        return null;
    }

    public void preLoadData() {

    }
}
