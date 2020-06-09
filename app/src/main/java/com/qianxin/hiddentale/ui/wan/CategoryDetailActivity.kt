package com.qianxin.hiddentale.ui.wan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.qianxin.hiddentale.R
import com.qianxin.hiddentale.adapter.MyFragmentPageAdapter
import com.qianxin.hiddentale.app.Constants
import com.qianxin.hiddentale.data.bean.wan.TreeBean
import com.qianxin.hiddentale.ui.wan.detail.CategoryArticleFragment
import com.qianxin.hiddentale.utils.ImageLoadUtil
import com.qianxin.hiddentale.utils.ToolbarHelper
import com.qianxin.hiddentale.utils.statusbar.StatusBarUtil
import kotlinx.android.synthetic.main.activity_category_detail.*

class CategoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setTranslucentStatus(this)
        setContentView(R.layout.activity_category_detail)
        ToolbarHelper.initFullBar(toolbar, this)
        toolbar.setNavigationOnClickListener { finish() }

        initData()
    }

    private fun initData() {
        val cid = intent.getIntExtra("cid", 0)
        val treeBean = intent.getSerializableExtra("CategoryBean") as TreeBean
        updateUi(treeBean)

        val mTitleList = ArrayList<String>()
        val mFragments = ArrayList<Fragment>()

        var initIndex = 0
        for (i in 0 until treeBean.children.size) {
            val children = treeBean.children[i]
            if (children.id == cid) {
                initIndex = i
            }

            mTitleList.add(children.name)
            mFragments.add(CategoryArticleFragment.getInstance(cid))
//            mFragments.add(TextFragment())
        }

        val myAdapter = MyFragmentPageAdapter(supportFragmentManager, mFragments, mTitleList)
        viewPager.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
        tabLayout.setupWithViewPager(viewPager)

        //选中条目
        viewPager.currentItem = initIndex
    }

    private fun updateUi(treeBean: TreeBean) {
        ImageLoadUtil.displayGaussFuzzy(blur, Constants.CATEGORY_DETAIL_IMG)
        tv_category_num.text = "${String.format(getString(R.string.string_tree_category_size), treeBean.children.size)}"
        toolbar.title = treeBean.name
    }

    companion object {
        fun start(context: Context, cid: Int, bean: TreeBean) {
            val intent = Intent(context, CategoryDetailActivity::class.java)
            intent.putExtra("cid", cid)
            intent.putExtra("CategoryBean", bean)
            context.startActivity(intent)
        }
    }
}
