package com.tw.speedbrowser

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tw.speedbrowser.adapter.CommonTabAdapter
import com.tw.speedbrowser.adapter.Fun
import com.tw.speedbrowser.adapter.PagerAdapter
import com.tw.speedbrowser.base.BaseActivity
import com.tw.speedbrowser.base.listener.OnPageChangeAdapter
import com.tw.speedbrowser.base.listener.RecentFavorListener
import com.tw.speedbrowser.base.utils.BitmapUtils
import com.tw.speedbrowser.base.utils.CommonUtils
import com.tw.speedbrowser.base.utils.LogUtils
import com.tw.speedbrowser.base.utils.SPUtils
import com.tw.speedbrowser.base.widget.SleTextButton
import com.tw.speedbrowser.fragment.FavorFragment
import com.tw.speedbrowser.fragment.RecentSearchFragment
import com.tw.speedbrowser.manager.DataManager
import com.tw.speedbrowser.manager.RecentSearchManager
import com.tw.speedbrowser.model.CommonTabModel
import com.tw.speedbrowser.model.FavorModel
import com.tw.speedbrowser.model.RecentSearchModel
import com.tw.speedbrowser.model.SearchType
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private val tag = "MainAcy"
    override fun initData() {
        DataManager.setSearchType(SPUtils.getInstance().getInt(SearchType.KEY_SEARCH_TYPE, SearchType.DEFAULT_TYPE))
        updateTopSearchView()


    }

    private var hasGoSearch = false
    private fun goToSearch(key: String? = "",force: Boolean = false) {
        if (key?.isNotEmpty() == true || force) {
            hasGoSearch = true
            et_search.clearFocus()
            CommonUtils.closeKeyBoard(this@MainActivity)
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_URL_VALUE, key)
            intent.putExtra(WebActivity.BACK_FROM_HOME, force)
            startActivity(intent)
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim)
        }
    }

    private fun download(iv: ImageView) {
        if (Build.VERSION.SDK_INT >= 23) {
            val rxPermissions = RxPermissions(this)
            val disposable = rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { aBoolean ->
                    if (aBoolean) {
                        CommonUtils.saveBitmap(this, BitmapUtils.getViewBitmap(iv))
                        Toast.makeText(this,getString(R.string.save_to_gallery),Toast.LENGTH_SHORT).show()
                    } else {
                    }
                }
        } else {
            CommonUtils.saveBitmap(this, BitmapUtils.getViewBitmap(iv))
            Toast.makeText(this,getString(R.string.save_to_gallery),Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRewardPopupWindow() {
        val popupView = LayoutInflater.from(baseContext)
            .inflate(R.layout.reward_popup_window_layout, null)
        var mPopupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val mIvCopy_paypal_me_link = popupView.findViewById<ImageView>(R.id.mIvCopy_paypal_me_link)
        val mIvCopy_paypal_me_email = popupView.findViewById<ImageView>(R.id.mIvCopy_paypal_me_email)

        val iv_ali_download = popupView.findViewById<ImageView>(R.id.iv_ali_download)
        val pay_ali = popupView.findViewById<ImageView>(R.id.pay_ali)
        val iv_wechat_download = popupView.findViewById<ImageView>(R.id.iv_wechat_download)
        val pay_wechat = popupView.findViewById<ImageView>(R.id.pay_wechat)
        pay_ali.setOnClickListener {
            download(pay_ali)
        }
        iv_ali_download.setOnClickListener {
            download(pay_ali)
        }

        iv_wechat_download.setOnClickListener {
            download(pay_wechat)
        }
        pay_wechat.setOnClickListener {
            download(pay_wechat)
        }

        mIvCopy_paypal_me_link.setOnClickListener {
            CommonUtils.copy("https://paypal.me/itachsheep?country.x=C2&locale.x=zh_XC", this)
            Toast.makeText(this, getString(R.string.copy_success), Toast.LENGTH_SHORT).show()
        }
        mIvCopy_paypal_me_email.setOnClickListener {
            CommonUtils.copy("itachsheep045@gmail.com", this)
            Toast.makeText(this, getString(R.string.copy_success), Toast.LENGTH_SHORT).show()
        }

        mPopupWindow.contentView = popupView
        mPopupWindow.isTouchable = true
        mPopupWindow.isOutsideTouchable = true
        mPopupWindow.isFocusable = true
        //在控件上方显示
        mPopupWindow.showAtLocation(reward, Gravity.BOTTOM,0,0)
    }

    private fun updateTopSearchView() {

        when (DataManager.getSearchType()) {
            SearchType.TYPE_BAI_DU -> {
                mIvSearchNet.setImageResource(R.drawable.baidu_icon)
            }
            SearchType.TYPE_SO_GOU -> {
                mIvSearchNet.setImageResource(R.mipmap.sougo)
            }
            SearchType.TYPE_GOOGLE -> {
                mIvSearchNet.setImageResource(R.drawable.google_logo)
            }
            SearchType.TYPE_BING -> {
                mIvSearchNet.setImageResource(R.drawable.bing_logo)
            }

        }

    }

    private fun showSelectNetPopupWindow() {
        val popupView = LayoutInflater.from(baseContext)
            .inflate(R.layout.select_net_popup_window_layout, null)
        var mPopupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val stb_baidu = popupView.findViewById<SleTextButton>(R.id.stb_baidu)
        val stb_sougou = popupView.findViewById<SleTextButton>(R.id.stb_sougou)
        val stb_google = popupView.findViewById<SleTextButton>(R.id.stb_google)
        val stb_bing = popupView.findViewById<SleTextButton>(R.id.stb_bing)
        //val type = SPUtils.getInstance().getInt(SearchType.KEY_SEARCH_TYPE, SearchType.TYPE_BAI_DU)
        val drawable = getDrawable(R.drawable.svg_check_circle)
        drawable?.setBounds(0,0,drawable.minimumWidth,drawable.minimumHeight);
        when (DataManager.getSearchType()) {
            SearchType.TYPE_BAI_DU -> {
                stb_baidu.isSelected = true
                stb_baidu.setCompoundDrawables(null,null,drawable, null)
                stb_sougou.setCompoundDrawables(null,null,null,null)
                stb_google.setCompoundDrawables(null,null,null,null)
                stb_bing.setCompoundDrawables(null,null,null,null)
            }
            SearchType.TYPE_SO_GOU -> {
                stb_sougou.isSelected = true
                stb_baidu.setCompoundDrawables(null,null,null, null)
                stb_sougou.setCompoundDrawables(null,null,drawable,null)
                stb_google.setCompoundDrawables(null,null,null,null)
                stb_bing.setCompoundDrawables(null,null,null,null)
            }
            SearchType.TYPE_GOOGLE -> {
                stb_google.isSelected = true
                stb_baidu.setCompoundDrawables(null,null,null, null)
                stb_sougou.setCompoundDrawables(null,null,null,null)
                stb_google.setCompoundDrawables(null,null,drawable,null)
                stb_bing.setCompoundDrawables(null,null,null,null)
            }
            SearchType.TYPE_BING -> {
                stb_bing.isSelected = true
                stb_baidu.setCompoundDrawables(null,null,null, null)
                stb_sougou.setCompoundDrawables(null,null,null,null)
                stb_google.setCompoundDrawables(null,null,null,null)
                stb_bing.setCompoundDrawables(null,null,drawable,null)
            }
        }
        stb_baidu.setOnClickListener {
            mPopupWindow.dismiss()
            DataManager.setSearchType(SearchType.TYPE_BAI_DU)
            updateTopSearchView()
            SPUtils.getInstance().put(SearchType.KEY_SEARCH_TYPE, DataManager.getSearchType())
        }
        stb_sougou.setOnClickListener {
            mPopupWindow.dismiss()
            DataManager.setSearchType(SearchType.TYPE_SO_GOU)
            updateTopSearchView()
            SPUtils.getInstance().put(SearchType.KEY_SEARCH_TYPE, DataManager.getSearchType())
        }
        stb_google.setOnClickListener {
            mPopupWindow.dismiss()
            DataManager.setSearchType(SearchType.TYPE_GOOGLE)
            updateTopSearchView()
            SPUtils.getInstance().put(SearchType.KEY_SEARCH_TYPE, DataManager.getSearchType())
        }

        stb_bing.setOnClickListener {
            mPopupWindow.dismiss()
            DataManager.setSearchType(SearchType.TYPE_BING)
            updateTopSearchView()
            SPUtils.getInstance().put(SearchType.KEY_SEARCH_TYPE, DataManager.getSearchType())
        }


        mPopupWindow.contentView = popupView
        mPopupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mPopupWindow.isTouchable = true
        mPopupWindow.isOutsideTouchable = true
        mPopupWindow.isFocusable = true
//        mPopupWindow.showAsDropDown(mIvSearchNetArrow)
        mPopupWindow.showAsDropDown(mIvSearchNetArrow, -mLLSearchNet.width, 0)
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btFaceBook,R.id.stbFaceBook -> {
                goToSearch("https://www.facebook.com/")
            }
            R.id.btYouTube,R.id.stbYouTube -> {
                goToSearch("https://www.youtube.com/")
            }
            R.id.btWikipedia,R.id.stbWikipedia -> {
                goToSearch("https://www.wikipedia.org/")
            }
        }
    }

    override fun initListener() {

        btFaceBook.setOnClickListener(this)
        stbFaceBook.setOnClickListener(this)
        btYouTube.setOnClickListener(this)
        stbYouTube.setOnClickListener(this)
        btWikipedia.setOnClickListener(this)
        stbWikipedia.setOnClickListener(this)

        mLLSearchNet.setOnClickListener {
            showSelectNetPopupWindow()
        }

        reward.setOnClickListener {
            showRewardPopupWindow()
        }


        tvSearch.setOnClickListener {
            if (et_search.text.isNotEmpty()) {
                LogUtils.d(tag, "tvSearch click: ${et_search.text.toString()}")
                goToSearch(et_search.text.toString())
            }
        }
        et_search.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (et_search.text.isNotEmpty()) {
                        goToSearch(et_search.text.toString())
                    }
                }
            }
            true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        setIntent(intent)
        super.onNewIntent(intent)
        LogUtils.d(tag, "onNewIntent ---> ")
    }

    private var mHasShowSoft = false
    override fun initView() {
//        sample_text.text = stringFromJNI()

        initViewPager()
    }

    private val mFragments = ArrayList<Fragment>()
    private val mTitles = ArrayList<String>()
    private val mTabModels = arrayListOf<CommonTabModel>()
    private val mCommonTabAdapter by lazy { CommonTabAdapter() }
    private val recentSearchFragment by lazy {
        RecentSearchFragment()
    }
    private val favorFragment by lazy {
        FavorFragment()
    }
    private fun initViewPager() {
        mFragments.clear()
        mTitles.clear()
        mTabModels.clear()

        mTitles.add(getString(R.string.recent_search))
        mTitles.add(getString(R.string.favor))

        val recentFavorListener = object : RecentFavorListener {
            override fun onRecentItemClick(data: RecentSearchModel?) {
                et_search.clearFocus()
                goToSearch(data?.title)
            }

            override fun onFavorItemClick(data: FavorModel?) {
                et_search.clearFocus()
                goToSearch(data?.netUrl)
            }

        }
        recentSearchFragment.setRecentFavorListener(recentFavorListener)
        favorFragment.setRecentFavorListener(recentFavorListener)

        mFragments.add(recentSearchFragment)
        mFragments.add(favorFragment)

        mTitles.forEachIndexed { index, title ->
            mTabModels.add(CommonTabModel(index == 0, title, CommonTabAdapter.LEVEL_MAIN_TYPE))
        }

        mCommonTabAdapter.setList(mTabModels)

        mRvTab.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
        mRvTab.adapter = mCommonTabAdapter

        val pagerAdapter = PagerAdapter(supportFragmentManager, mFragments, mTitles.toTypedArray())
        mViewPager.adapter = pagerAdapter
        mViewPager.offscreenPageLimit = 2
        Fun.registerListener(mViewPager, mCommonTabAdapter, mRvTab)

        mIvDeleteHistory.setOnClickListener {
            RecentSearchManager.clear()
            recentSearchFragment.refreshRecentSearchData()
        }

        mViewPager.addOnPageChangeListener(object : OnPageChangeAdapter(){
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    mIvDeleteHistory.visibility = View.VISIBLE
                } else {
                    mIvDeleteHistory.visibility = View.GONE
                }
            }
        })



    }

    override fun onResume() {
        super.onResume()
        LogUtils.d(tag,"onResume")
        if(hasGoSearch) {
            mIvNext.visibility = View.VISIBLE
            mIvNext.setOnClickListener {
                goToSearch(force = true)
            }
        }
        recentSearchFragment.refreshRecentSearchData()
    }

    override fun onPause() {
        super.onPause()
        et_search.clearFocus()
        CommonUtils.closeKeyBoard(this)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    /**
     * A native method that is implemented by the 'speedbrowser' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.e(tag, "onDestroy")
    }


    companion object {
        // Used to load the 'speedbrowser' library on application startup.
//        init {
//            System.loadLibrary("speedbrowser")
//        }
    }


}