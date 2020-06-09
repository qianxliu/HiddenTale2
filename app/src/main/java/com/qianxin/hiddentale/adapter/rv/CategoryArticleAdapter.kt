package com.qianxin.hiddentale.adapter.rv

import android.annotation.SuppressLint
import android.text.Html
import android.view.View
import android.view.ViewGroup
import com.qianxin.hiddentale.R
import com.qianxin.hiddentale.data.bean.wan.ArticlesBean
import com.qianxin.hiddentale.utils.ImageLoadUtil
import com.qianxin.hiddentale.view.webview.WebViewActivity
import com.tuju.jetpackfirstdemo.base.baseadapter.BaseRecyclerViewAdapter
import com.tuju.jetpackfirstdemo.base.baseadapter.BaseRecyclerViewHolder
import kotlinx.android.synthetic.main.item_category_article.view.*

class CategoryArticleAdapter : BaseRecyclerViewAdapter<ArticlesBean>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<ArticlesBean> {
        return ViewHolder(getView(parent, R.layout.item_category_article))
    }

    inner class ViewHolder(view: View) : BaseRecyclerViewHolder<ArticlesBean>(view) {
        @SuppressLint("SetTextI18n")
        override fun onBaseBindViewHolder(bean: ArticlesBean, position: Int) {
            //image
            if (bean.envelopePic == null || bean.envelopePic.isEmpty()) {
                view.iv_image.visibility = View.GONE
            } else {
                view.iv_image.visibility = View.VISIBLE
                ImageLoadUtil.displayListImage(view.iv_image, bean.envelopePic, 1)
            }
            view.tv_title.text = Html.fromHtml(bean.title)
            view.textView2.text = bean.niceDate
            view.textView3.text = "${view.context.getString(R.string.string_dian)}${bean.author}"

            //checkbox
            view.vb_collect.isChecked = bean.collect

            //onclick
            view.cl_parent.setOnClickListener {
                WebViewActivity.loadUrl(view.context, bean.link, bean.title)
            }
        }
    }

}