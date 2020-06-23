package com.qianxin.hiddentale.utils

import android.content.Context
import com.qianxin.hiddentale.network.HttpClient.Companion.getInstance
import com.qianxin.hiddentale.network.cache.ACache
import com.qianxin.hiddentale.repository.GankRepository
import com.qianxin.hiddentale.repository.WanRepository
import com.qianxin.hiddentale.repository.WelfareRepository
import com.qianxin.hiddentale.ui.gank.android.GankViewModelFactory
import com.qianxin.hiddentale.ui.gank.customer.CsutomViewModelFactory
import com.qianxin.hiddentale.ui.gank.welfare.WelfareViewModelFactory
import com.qianxin.hiddentale.ui.search.SearchViewModelFactory
import com.qianxin.hiddentale.ui.wan.article.ArticleViewModelFactory
import com.qianxin.hiddentale.ui.wan.home.BannerViewModelFactory
import com.qianxin.hiddentale.ui.wan.navi.NaviViewModelFactory
import com.qianxin.hiddentale.ui.wan.tree.TreeViewModelFactory

object InjectorUtil {

    private var welfareRepository: WelfareRepository? = null
    private var gankRepository: GankRepository? = null
    private var wanRepository: WanRepository? = null

    //获取宝图仓库
    private fun getWelfareRepository(acache: ACache): WelfareRepository {
        if (welfareRepository == null) {
            synchronized(InjectorUtil::class.java) {
                if (welfareRepository == null) {
                    welfareRepository = WelfareRepository.getInstantce(getInstance(), acache)
                }
            }
        }
        return welfareRepository!!
    }

    //获取干货数据仓库
    private fun getGankRepository(acache: ACache): GankRepository {
        if (gankRepository == null) {
            synchronized(InjectorUtil::class.java) {
                if (gankRepository == null) {
                    gankRepository = GankRepository.getInstance(getInstance(), acache)
                }
            }
        }
        return gankRepository!!
    }


    private fun getWanRepository(acache: ACache): WanRepository {
        if (wanRepository == null) {
            synchronized(InjectorUtil::class.java) {
                if (wanRepository == null) {
                    wanRepository = WanRepository.getInstance(getInstance(), acache)
                }
            }
        }
        return wanRepository!!
    }

    //==============获取ViewModel创建工厂=============

    //获取宝图工厂类
    fun getWelfFactory(context: Context?) = WelfareViewModelFactory(getWelfareRepository(ACache.get(context)))

    fun getGankFactory(context: Context?) = GankViewModelFactory(getGankRepository(ACache.get(context)))

    fun getCustomFactory(context: Context?) = CsutomViewModelFactory(getGankRepository(ACache.get(context)))

    //获取导航数据工厂
    fun getNaviJsonFactory(context: Context?) = NaviViewModelFactory(getWanRepository(ACache.get(context)))

    //获取知识体系
    fun getTreeJsonFactory(context: Context?) = TreeViewModelFactory(getWanRepository(ACache.get(context)))

    //获取玩安卓首页
    fun getWanBannerFactory(context: Context?) = BannerViewModelFactory(getWanRepository(ACache.get(context)))

    //文章列表
    fun getArticleViewModelFactory(context: Context?) = ArticleViewModelFactory(getWanRepository(ACache.get(context)))

    //搜索
    fun getSearchViewModelFactory(context: Context?): SearchViewModelFactory {
        val acache = ACache.get(context)
        return SearchViewModelFactory(getWanRepository(acache), getGankRepository(acache))
    }
}