package com.qianxin.hiddentale

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.qianxin.hiddentale.adapter.MyFragmentPageAdapter
import com.qianxin.hiddentale.app.ConstantsImageUrl
import com.qianxin.hiddentale.ui.gank.GankFragment
import com.qianxin.hiddentale.ui.map.MapFragment
import com.qianxin.hiddentale.ui.navi_menu.AboutActivity
import com.qianxin.hiddentale.ui.navi_menu.IssueActivity
import com.qianxin.hiddentale.ui.navi_menu.NavHomePageActivity
import com.qianxin.hiddentale.ui.navi_menu.ScanDownActivity
import com.qianxin.hiddentale.ui.search.SearchActivity
import com.qianxin.hiddentale.ui.wan.WanFragment
import com.qianxin.hiddentale.utils.*
import com.qianxin.hiddentale.utils.PermissionHandler.isHandlePermission
import com.qianxin.hiddentale.utils.statusbar.StatusBarUtil
import com.qianxin.hiddentale.view.webview.WebViewActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } else {
                arrayOf(
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        permissions.forEach {
            isHandlePermission(this, it)
        }


        isLaunch = true
//        initStatusView()
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this@MainActivity, drawer_layout,
                CommonUtils.getColor(R.color.colorTheme))

        initContentFragment()
        initDrawerLayout()
        initListener()
    }

//    fun initStatusView() {
//        view_status.layoutParams.height = StatusBarUtil.getStatusBarHeight(this@MainActivity)
//    }

    private fun initContentFragment() {
        val mFragmentList: ArrayList<Fragment> = ArrayList()
        mFragmentList.add(MapFragment.getInstance())
        mFragmentList.add(WanFragment.getInstance())
        mFragmentList.add(GankFragment.getInstance())


        val adapter = MyFragmentPageAdapter(supportFragmentManager, mFragmentList)
        vp_content.adapter = adapter
        vp_content.offscreenPageLimit = 2
        vp_content.addOnPageChangeListener(this@MainActivity)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setCurrentItem(0)
    }

    private fun initDrawerLayout() {
//        var view = layoutInflater.inflate(R.layout.nav_header_main, null, false)
        val view = nav_view.inflateHeaderView(R.layout.nav_header_main)
//        val iv_avatar = view.findViewById<ImageView>(R.id.iv_avatar)
//        val ll_nav_exit = view.findViewById<LinearLayout>(R.id.ll_nav_exit)
//        val ll_nav_homepage = view.findViewById<LinearLayout>(R.id.ll_nav_homepage)
//        val ll_nav_scan_download = view.findViewById<LinearLayout>(R.id.ll_nav_scan_download)

        ImageLoadUtil.displayCircle(view.iv_avatar, ConstantsImageUrl.IC_AVATAR)
        view.ll_nav_exit.setOnClickListener(this)
        view.iv_avatar.setOnClickListener(this)

        //导航栏其他条目点击事件
        view.ll_nav_homepage.setOnClickListener(listener)
        view.ll_nav_scan_download.setOnClickListener(listener)
        view.ll_nav_deedback.setOnClickListener(listener)
        view.ll_nav_about.setOnClickListener(listener)
        view.ll_nav_collect.setOnClickListener(listener)
    }

    private fun initListener() {
        //init title bar
        ll_title_menu.setOnClickListener(this)
        iv_title_one.setOnClickListener(this)
        iv_title_two.setOnClickListener(this)
        iv_title_three.setOnClickListener(this)
    }

    /*
     * 切换界面
     */
    private fun setCurrentItem(position: Int) {
        var isOne = false
        var isTwo = false
        var isThree = false
        when (position) {
            0 -> isOne = true
            1 -> isTwo = true
            2 -> isThree = true
        }
        vp_content.currentItem = position
        iv_title_one.isSelected = isOne
        iv_title_two.isSelected = isTwo
        iv_title_three.isSelected = isThree
    }

    //防抖点击事件
    private val listener = object : PerfectClickListener() {
        override fun onNoDoubleClick(v: View?) {
            drawer_layout.closeDrawer(GravityCompat.START)
            drawer_layout.postDelayed({
                when (v?.id) {
                    R.id.ll_nav_homepage -> {
//                        this@MainActivity.showToast("首页")
                        NavHomePageActivity.start(this@MainActivity)
                    }
                    R.id.ll_nav_scan_download -> {
                        ScanDownActivity.start(this@MainActivity) //扫码下载
                    }
                    R.id.ll_nav_deedback -> {
//                        showToast("问题反馈")
                        IssueActivity.start(this@MainActivity)
                    }
                    R.id.ll_nav_about -> {
//                        showToast("关于云阅")
                        AboutActivity.start(this@MainActivity)
                    }
                    R.id.ll_nav_collect -> {
                        showToast("我的收藏")
                    }
                }
            }, 260)
        }
    }

    //点击事件
    override fun onClick(v: View?) {
        when (v?.id) {
            (R.id.ll_title_menu) -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
            (R.id.iv_title_one) -> {
                if (vp_content.currentItem != 0) {
                    setCurrentItem(0)
                }
            }
            (R.id.iv_title_two) -> {
                if (vp_content.currentItem != 1) {
                    setCurrentItem(1)
                }
            }
            (R.id.iv_title_three) -> {
                if (vp_content.currentItem != 2) {
                    setCurrentItem(2)
                }
            }
            (R.id.iv_avatar) -> {
                WebViewActivity.loadUrl(this@MainActivity, getString(R.string.github))
//                this@MainActivity.showToast("头像进入Github")
            }
            (R.id.ll_nav_exit) -> {
                //退出应用
                finish()
            }
        }
    }

    /*
     * 获取剪贴板链接
     */
    private fun getClipContent() {
        val clipContent = BaseTools.getClipContent()
        if (clipContent.isNotEmpty()) {
            this@MainActivity.showToast(clipContent)
        }
    }

    //搜索菜单
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
//                this@MainActivity.showToast("搜索")
                SearchActivity.newInstance(this@MainActivity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //viewpager切换监听
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> setCurrentItem(0)
            1 -> setCurrentItem(1)
            2 -> setCurrentItem(2)
        }
    }

    override fun onResume() {
        super.onResume()
        getClipContent()
    }

    override fun onDestroy() {
        super.onDestroy()
        isLaunch = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                // 不退出程序，进入后台
                moveTaskToBack(true)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    companion object {
        var isLaunch: Boolean = false

        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}
