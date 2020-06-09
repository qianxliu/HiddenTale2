package com.qianxin.hiddentale.adapter.rv

import android.view.View
import android.view.ViewGroup
import com.qianxin.hiddentale.R
import com.qianxin.hiddentale.data.bean.search.SearchHotTagBean
import com.tuju.jetpackfirstdemo.base.baseadapter.BaseRecyclerViewAdapter
import com.tuju.jetpackfirstdemo.base.baseadapter.BaseRecyclerViewHolder
import kotlinx.android.synthetic.main.item_navi_tag.view.*

class SearchTagKeyAdapter : BaseRecyclerViewAdapter<SearchHotTagBean>() {

    private var callback: HotTagCallback? = null

    fun setTagClickCallback(callback: HotTagCallback) {
        this.callback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<SearchHotTagBean> {
        return ViewHolder(getView(parent, R.layout.item_navi_tag))
    }

    inner class ViewHolder(view: View) : BaseRecyclerViewHolder<SearchHotTagBean>(view) {
        override fun onBaseBindViewHolder(bean: SearchHotTagBean, position: Int) {
            view.tv_navi_tag.text = bean.name
            view.tv_navi_tag.setOnClickListener {
                callback?.tagClick(bean)
            }
        }
    }

    interface HotTagCallback {
        fun tagClick(bean: SearchHotTagBean);
    }
}