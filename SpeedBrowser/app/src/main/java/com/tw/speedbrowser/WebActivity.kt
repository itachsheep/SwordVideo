package com.tw.speedbrowser

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.tw.speedbrowser.base.utils.*
import com.tw.speedbrowser.base.widget.KeyboardStatusDetector
import com.tw.speedbrowser.base.widget.SleTextButton
import com.tw.speedbrowser.fragment.FavorFragment
import com.tw.speedbrowser.fragment.RecentSearchFragment
import com.tw.speedbrowser.manager.DataManager
import com.tw.speedbrowser.manager.FavorManager
import com.tw.speedbrowser.manager.RecentSearchManager
import com.tw.speedbrowser.model.CommonTabModel
import com.tw.speedbrowser.model.FavorModel
import com.tw.speedbrowser.model.RecentSearchModel
import com.tw.speedbrowser.model.SearchType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.activity_webview.et_search
import kotlinx.android.synthetic.main.activity_webview.iv_clear_key
import kotlinx.android.synthetic.main.activity_webview.mIvDeleteHistory
import kotlinx.android.synthetic.main.activity_webview.mIvSearchNet
import kotlinx.android.synthetic.main.activity_webview.mIvSearchNetArrow
import kotlinx.android.synthetic.main.activity_webview.mLLSearchNet
import kotlinx.android.synthetic.main.activity_webview.mRvTab
import kotlinx.android.synthetic.main.activity_webview.mViewPager
import kotlinx.android.synthetic.main.activity_webview.sl_et_content
import kotlinx.android.synthetic.main.activity_webview.tvSearch


/**
 *
 * @author: Baron
 * @date  : 2023/2/16 11:50
 * @email : baron@niubi.im
 */
class WebActivity : BaseActivity() {
    companion object {
        const val KEY_URL_VALUE = "key_url_value"
        const val BACK_FROM_HOME = "back_from_home"
    }

    private val tag = "WebActivity"

    private var mVisitUrl = ""
    private var mBackFromHome = false

    override fun initArgs(bundle: Bundle?): Boolean {
        initVisitUrl()
        return true
    }

    private fun initVisitUrl() {
        val stringExtra = intent.getStringExtra(KEY_URL_VALUE) ?: ""
        mVisitUrl = stringExtra
        mBackFromHome = intent.getBooleanExtra(BACK_FROM_HOME,false)
        LogUtils.d(tag, "initVisitUrl mVisitUrl: $mVisitUrl, mBackFromHome: $mBackFromHome")
    }

    override fun onNewIntent(intent: Intent?) {
        setIntent(intent)
        super.onNewIntent(intent)
        DataManager.setSearchType(SPUtils.getInstance().getInt(SearchType.KEY_SEARCH_TYPE, SearchType.DEFAULT_TYPE))
        updateTopSearchView()

        initVisitUrl()

        if(mBackFromHome) {
            //
        }  else {
            et_search.setText(mVisitUrl)
            goToSearchWithUrl()
        }
    }

    override fun initData() {
        val webSettings: WebSettings = mWebView.settings
        //设置WebView属性，能够执行Javascript脚本
        webSettings.javaScriptEnabled = true
        //设置可以访问文件
        webSettings.allowFileAccess = true
        webSettings.setAppCacheEnabled(true)
        webSettings.domStorageEnabled = true
        webSettings.supportMultipleWindows()

        //设置不支持缩放
        webSettings.setSupportZoom(false)
        webSettings.builtInZoomControls = false
        webSettings.allowContentAccess = true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS;
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.savePassword = true
        webSettings.saveFormData = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ThemeModeUtils.isNight(this)) { //判断如果系统是深色主题
                mWebView.settings.forceDark = WebSettings.FORCE_DARK_ON //强制开启webview深色主题模式
            } else {
                mWebView.settings.forceDark = WebSettings.FORCE_DARK_OFF;
            }
        }

        mWebView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
//                LogUtils.d(tag, "newProgress: $newProgress")
                if (newProgress == 100) {
                    mProgress.progress = 0
                    mProgress.visibility = View.GONE
                    updateGoBackForward()
                } else {
                    mProgress.progress = newProgress
                    mProgress.visibility = View.VISIBLE
                }
            }
        }

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                //LogUtils.d(tag, "shouldOverrideUrlLoading request: ${request?.getUrl().toString()}")
                val url = request?.url.toString() ?: ""
                LogUtils.d(tag, "shouldOverrideUrlLoading url: $url, title: ${view?.title}")
                try {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        //其他自定义的scheme
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                } catch (e: Exception) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return true //没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }
                return false
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
                LogUtils.d(tag, "onPageFinished  ---> mCurKeyWord: $mCurKeyWord, url: $url, title: ${webView?.title}")
                super.onPageFinished(webView, url)
                if (mCurKeyWord?.isNotEmpty() == true) {
                    val model = RecentSearchModel()
                    model.title = mCurKeyWord
                    model.netUrl = url ?: ""
                    RecentSearchManager.put(model)
                }

//                val js2 = """javascript: (function () {
//                    var aList = document.getElementsByTagName("img");
//                    var parentList = [];
//                    for (var i = 0; i < aList.length; i++) {
//                        parentList = parentList.concat([aList[i].parentElement]);
//                    }
//                    for (var i = 0; i < aList.length; i++) {
//                        parentList[i].style.display = "none";
//                    }
//                })();"""
//                webView?.loadUrl(js2)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                LogUtils.d(tag, "onReceivedError url: $request, error: $error")
                super.onReceivedError(view, request, error)
            }
        }

        et_search.setText(mVisitUrl)
        goToSearchWithUrl()
    }

    private fun updateGoBackForward() {
        if (mWebView.canGoBack()) {
            mIvBack.setImageResource(R.drawable.svg_arrow_right_black)
        } else {
            mIvBack.setImageResource(R.drawable.svg_arrow_right)
        }

        if (mWebView.canGoForward()) {
            mIvGoForward.setImageResource(R.drawable.svg_arrow_right_black)
        } else {
            mIvGoForward.setImageResource(R.drawable.svg_arrow_right)
        }
    }

    override fun initListener() {
        mIvSetting.setOnClickListener {
            showSettingPopupWindow()
        }
        mIvEdit.setOnClickListener {
            et_search.setText(mNetUrl.text.toString())
            et_search.setSelection(mNetUrl.text.toString().length)
        }

        mIvCopy.setOnClickListener {
            CommonUtils.copy(mNetUrl.text.toString(), this)
            Toast.makeText(this, getString(R.string.copy_success), Toast.LENGTH_SHORT).show()
        }
        mLLSearchNet.setOnClickListener { showSelectNetPopupWindow() }
        // mIvSearchNetArrow.setOnClickListener {showSelectNetPopupWindow() }

        mllBottom.setOnClickListener {
            LogUtils.d(tag, "mllBottom on click")
        }

        mIvHome.setOnClickListener {
            val intent = Intent(this@WebActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim)
        }

        mLLBack.setOnClickListener {
            LogUtils.d(tag, "mWebView canGoBack: ${mWebView.canGoBack()}")
            if (mWebView.canGoBack()) {
                mWebView.goBack()
            }
        }

        mLLGoForward.setOnClickListener {
            LogUtils.d(tag, "mWebView canGoForward: ${mWebView.canGoForward()}")
            if (mWebView.canGoForward()) {
                mWebView.goForward()
            }
        }

        iv_clear_key.setOnClickListener {
            et_search.setText("")
        }

        tvSearch.setOnClickListener {
            if (et_search.text.isEmpty()) {
                return@setOnClickListener
            }
            goToSearchWithKeyWords()
        }

        mEditPanel.setOnClickListener {
            et_search.clearFocus()
            CommonUtils.closeKeyBoard(this@WebActivity)
        }

        mViewPager.setOnClickListener {
            CommonUtils.closeKeyBoard(this@WebActivity)
            et_search.clearFocus()
        }


        mIvFavor.setOnClickListener {
            mWebView.url?.apply {
                if (this.isNotEmpty()) {
                    val favorModel = FavorModel()
                    favorModel.netUrl = this
                    favorModel.title = mWebView.title ?: ""
                    FavorManager.put(favorModel)
                }
                Toast.makeText(this@WebActivity,getString(R.string.favor_success),Toast.LENGTH_SHORT).show()
            }
        }
        val detector = KeyboardStatusDetector()
        detector.registerActivity(this)
        detector.setmVisibilityListener {
            if (!it) {
                et_search.clearFocus()
            }
        }

        et_search.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (et_search.text.isNotEmpty()) {
                        goToSearchWithKeyWords()
                    } else {
                        CommonUtils.closeKeyBoard(this)
                    }
                }
            }
            true
        }

        et_search.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            LogUtils.d(tag, "onFocusChange hasFocus: $hasFocus ")
            sl_et_content.isSelected = hasFocus
            if (hasFocus) {
                mEditPanel.visibility = View.VISIBLE
                mNetUrl.text = mWebView.url
                mNetTitle.text = mWebView.title
                mEditPanel.postDelayed({recentSearchFragment.refreshRecentSearchData()},300)
            } else {
                mEditPanel.visibility = View.GONE
            }
        }



        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.apply {
                    if (this.isNotEmpty()) {
                        iv_clear_key.visibility = View.VISIBLE
                    } else {
                        iv_clear_key.visibility = View.GONE
                    }
                }
            }

        })
    }

    private fun goToSearchWithUrl() {
        CommonUtils.closeKeyBoard(this)
        var url = spliceHttpUrl(et_search.text.toString())
        LogUtils.d(tag,"goToSearchWithUrl url: $url")
        mWebView.loadUrl(url)

    }

    private var mCurKeyWord = ""
    private fun goToSearchWithKeyWords() {
        CommonUtils.closeKeyBoard(this)
        mCurKeyWord = et_search.text.toString()
        var url = getSearchUrlWithKeyWords(mCurKeyWord)
        LogUtils.d(tag,"goToSearchWithKeyWords mCurKeyWord: $mCurKeyWord")
        mWebView.loadUrl(url)
    }

    override fun initView() {
        DataManager.setSearchType(SPUtils.getInstance().getInt(SearchType.KEY_SEARCH_TYPE, SearchType.DEFAULT_TYPE))
        updateTopSearchView()
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

        val recentFavorListener = object : RecentFavorListener{
            override fun onRecentItemClick(data: RecentSearchModel?) {
                et_search.setText(data?.title)
                et_search.clearFocus()
                goToSearchWithKeyWords()
            }

            override fun onFavorItemClick(data: FavorModel?) {
                et_search.setText(data?.netUrl)
                et_search.clearFocus()
                goToSearchWithUrl()
            }

        }
        recentSearchFragment.setRecentFavorListener(recentFavorListener)
        favorFragment.setRecentFavorListener(recentFavorListener)
        mFragments.add(recentSearchFragment)
        mFragments.add(favorFragment)

        mTitles.forEachIndexed { index, title ->
            mTabModels.add(CommonTabModel(index == 0, title, CommonTabAdapter.LEVEL_WEB_TYPE))
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

    override fun onBackPressed() {
        if (mEditPanel.visibility == View.VISIBLE) {
            CommonUtils.closeKeyBoard(this)
            mEditPanel.visibility = View.GONE
            return
        }

        if (mWebView.canGoBack()) {
            mWebView.goBack()
        } else {
            finish()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_webview
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
                    } else {
                    }
                }
        } else {
            CommonUtils.saveBitmap(this, BitmapUtils.getViewBitmap(iv))
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
        mPopupWindow.showAtLocation(mllBottom, Gravity.BOTTOM,0,0)
    }

    private fun showSettingPopupWindow() {
        val popupView = LayoutInflater.from(baseContext)
            .inflate(R.layout.setting_popup_window_layout, null)

        val popupHeight = DensityUtil.dp2px(240f)
        var mPopupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            popupHeight
        )
        mPopupWindow.contentView = popupView
        mPopupWindow.isTouchable = true
        mPopupWindow.isOutsideTouchable = true
        mPopupWindow.isFocusable = true

//        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        val location = IntArray(2)
        mIvSetting.getLocationInWindow(location)

        val llSwitchTheme = popupView.findViewById<LinearLayout>(R.id.llSwitchTheme)
        val llContactMe = popupView.findViewById<LinearLayout>(R.id.llContactMe)
        val stbContactEmail = popupView.findViewById<SleTextButton>(R.id.stbContactEmail)
        val ivTheme = popupView.findViewById<ImageView>(R.id.iv_theme)
        val reward = popupView.findViewById<SleTextButton>(R.id.reward)

        mCurThemeIsNight = ThemeModeUtils.isNight(baseContext)
        LogUtils.d(tag,"showSettingPopupWindow mCurThemeIsNight: $mCurThemeIsNight")
        ivTheme.setImageResource(if(mCurThemeIsNight) R.drawable.svg_white_theme else R.drawable.svg_dark_theme )

        reward.setOnClickListener {
            mPopupWindow.dismiss()
            showRewardPopupWindow()
        }

        llSwitchTheme.setOnClickListener {
            mPopupWindow.dismiss()
            changeTheme()
        }

        llContactMe.setOnClickListener {
//            mPopupWindow.dismiss()
            CommonUtils.copy(stbContactEmail.text.toString(), this)
            Toast.makeText(this, getString(R.string.copy_success), Toast.LENGTH_SHORT).show()
        }

        //在控件上方显示
        mPopupWindow.showAtLocation(mllBottom, Gravity.NO_GRAVITY, 0, location[1] - popupHeight);


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

    private fun spliceHttpUrl(url: String): String {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url
        } else if (url.contains(".com") || url.contains(".cn")) {
            return "https://".plus(url)
        } else if (url.startsWith("www.")) {
            return "https://".plus(url)
        } else {
            return getSearchUrlWithKeyWords(url)
        }
    }

    private fun getSearchUrlWithKeyWords(keyWords: String): String {
        var url = ""
        mCurKeyWord = keyWords
        when (DataManager.getSearchType()) {
            SearchType.TYPE_BAI_DU -> {
                url = "https://www.baidu.com/s?wd=".plus(keyWords)
            }
            SearchType.TYPE_SO_GOU -> {
                url = "https://www.sogou.com/web?query=".plus(keyWords)
            }
            SearchType.TYPE_GOOGLE -> {
                url = "https://www.google.com/search?q=".plus(keyWords)
            }
            SearchType.TYPE_BING -> {
                url = "https://www.bing.com/search?q=".plus(keyWords)
            }
        }
        return url
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.e(tag, "onDestroy")
    }

    private var mCurThemeIsNight = false
    private fun changeTheme() {
        LogUtils.d(tag,"changeTheme: ${ThemeModeUtils.isNight(baseContext)}");
        //RippleAnimation.updateBackground(getActivity().getWindow().getDecorView())
        mCurThemeIsNight = if (ThemeModeUtils.isNight(baseContext)) {
            // 如果是夜间模式  切换到白天模式
            ThemeModeUtils.setThemeMode(ThemeModeUtils.MODE_NIGHT_NO, true)
            true
        } else {
            // 如果是白天主题  切换到夜间模式
            ThemeModeUtils.setThemeMode(ThemeModeUtils.MODE_NIGHT_YES, true)
            false
        }
        finish()
    }
}