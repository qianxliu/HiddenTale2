package com.qianxin.hiddentale.adapter.rv

import android.text.Html
import android.view.View
import android.view.ViewGroup
import com.qianxin.hiddentale.R
import com.qianxin.hiddentale.data.bean.wan.TreeBean
import com.qianxin.hiddentale.ui.wan.CategoryDetailActivity
import com.qianxin.hiddentale.utils.CommonUtils
import com.tuju.jetpackfirstdemo.base.baseadapter.BaseRecyclerViewAdapter
import com.tuju.jetpackfirstdemo.base.baseadapter.BaseRecyclerViewHolder
import kotlinx.android.synthetic.main.item_tree_tag.view.*

class TreeTagAdapter(private val treeBean: TreeBean) : BaseRecyclerViewAdapter<TreeBean.TreeChildren>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TreeViewHolder(getView(parent, R.layout.item_tree_tag))

    inner class TreeViewHolder(view: View) : BaseRecyclerViewHolder<TreeBean.TreeChildren>(view) {
        override fun onBaseBindViewHolder(bean: TreeBean.TreeChildren, position: Int) {
            view.tv_tree_tag.text = Html.fromHtml(bean.name)
            view.tv_tree_tag.setTextColor(CommonUtils.randomColor())
            view.tv_tree_tag.setOnClickListener {
//                view.context.showToast(bean.name)
                CategoryDetailActivity.start(view.context, bean.id, treeBean)
            }
        }
    }

}