package com.tw.speedbrowser.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tw.speedbrowser.R
import com.tw.speedbrowser.adapter.RecentSearchAdapter
import com.tw.speedbrowser.base.BaseLazyLoadFragment
import com.tw.speedbrowser.base.listener.RecentFavorListener
import com.tw.speedbrowser.base.utils.LogUtils
import com.tw.speedbrowser.manager.RecentSearchManager
import kotlinx.android.synthetic.main.layout_fragment_recent.*

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 09:57
 * @email : baron@niubi.im
 */
class RecentSearchFragment:BaseLazyLoadFragment() {

    private val TAG = "RecentSearchFragment"

    private val mRecentSearchAdapter: RecentSearchAdapter by lazy {
        RecentSearchAdapter()
    }

    override fun isLazyLoadData(): Boolean {
        return false
    }

    override fun initListener() {
        mRecentSearchAdapter.setOnItemClickListener { adapter, view, position ->
            recentFavorListener?.onRecentItemClick(mRecentSearchAdapter.data[position])
        }
        mRecentSearchAdapter.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.mIvClear -> {
                    adapter.data.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    RecentSearchManager.remove(position)
                }
            }
        }
    }

    private var recentFavorListener: RecentFavorListener ?= null
    fun setRecentFavorListener(l: RecentFavorListener?) {
        recentFavorListener = l
    }

    fun refreshRecentSearchData() {
        val list = RecentSearchManager.get()
        mRecentSearchAdapter.setList(list)
        if (list.isNotEmpty()) {
            mIvEmpty?.visibility = View.GONE
        } else {
            mIvEmpty?.visibility = View.VISIBLE
        }
    }


    override fun setLayoutId(): Int {
        return R.layout.layout_fragment_recent
    }

    override fun initViewModel() {

    }

    override fun initData() {
        super.initData()
        mRecyclerView.adapter = mRecentSearchAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
//        mRecyclerView.addItemDecoration(
//            dividerBuilder {
//                it.color(ResUtils.getColor(R.color.line_2_D9D9D9))
//                it.height(1f.dp)
//            }.build()
//        )
    }

    override fun onResume() {
        super.onResume()
        LogUtils.d(TAG,"onResume")
        refreshRecentSearchData()
    }

    override fun initView(view: View?) {

    }

}