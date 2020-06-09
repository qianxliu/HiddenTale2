package com.qianxin.hiddentale.ui.navi_menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qianxin.hiddentale.R
import com.qianxin.hiddentale.utils.ShareUtils
import com.qianxin.hiddentale.utils.ToolbarHelper
import com.qianxin.hiddentale.utils.statusbar.StatusBarUtil
import kotlinx.android.synthetic.main.activity_nav_home_page.*

class NavHomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 4.4 标题透明
        StatusBarUtil.setTranslucentStatus(this)
        setContentView(R.layout.activity_nav_home_page)
        // 解决7.0以上系统 滑动到顶部 标题裁减一半的问题
//        setSupportActionBar(binding.detailToolbar);
        ToolbarHelper.initFullBar(detail_toolbar, this)
        detail_toolbar.navigationIcon = null

        fab_share.setOnClickListener {
            ShareUtils.share(this, R.string.string_share_text)
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, NavHomePageActivity::class.java))
        }
    }
}
