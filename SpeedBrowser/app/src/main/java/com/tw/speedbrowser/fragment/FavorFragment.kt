package com.tw.speedbrowser.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tw.speedbrowser.R
import com.tw.speedbrowser.adapter.FavorAdapter
import com.tw.speedbrowser.adapter.dividerBuilder
import com.tw.speedbrowser.base.BaseLazyLoadFragment
import com.tw.speedbrowser.base.listener.RecentFavorListener
import com.tw.speedbrowser.base.utils.LogUtils
import com.tw.speedbrowser.base.utils.ResUtils
import com.tw.speedbrowser.base.utils.dp
import com.tw.speedbrowser.manager.FavorManager
import com.tw.speedbrowser.manager.RecentSearchManager
import kotlinx.android.synthetic.main.layout_fragment_favor.*

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 09:57
 * @email : baron@niubi.im
 */
class FavorFragment:BaseLazyLoadFragment() {
    private val TAG = "RecentSearchFragment"
    private val mFavorAdapter: FavorAdapter by lazy {
        FavorAdapter()
    }

    private var recentFavorListener: RecentFavorListener?= null
    fun setRecentFavorListener(l: RecentFavorListener?) {
        recentFavorListener = l
    }

    override fun isLazyLoadData(): Boolean {
        return false
    }

    override fun initListener() {
        mFavorAdapter.setOnItemClickListener { adapter, view, position ->
            recentFavorListener?.onFavorItemClick(mFavorAdapter.data[position])
        }

        mFavorAdapter.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.mIvClear -> {
                    adapter.data.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    FavorManager.remove(position)
                }
            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.layout_fragment_favor
    }

    override fun initViewModel() {

    }

    override fun initData() {
        super.initData()

        mRecyclerView.adapter = mFavorAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        /*mRecyclerView.addItemDecoration(
            dividerBuilder {
                it.color(ResUtils.getColor(R.color.line_2_D9D9D9))
                it.height(1f.dp)
            }.build()
        )*/
    }

    override fun onResume() {
        super.onResume()
        LogUtils.d(TAG,"onResume")
        val list = FavorManager.get()
        if (list.isNotEmpty()) {
            mFavorAdapter.setList(list)
            mIvEmpty.visibility = View.GONE
        } else {
            mIvEmpty.visibility = View.VISIBLE
        }

    }

    override fun initView(view: View?) {

    }
}