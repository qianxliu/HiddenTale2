package com.qianxin.hiddentale.network.service

import com.qianxin.hiddentale.data.bean.NaviJsonBean
import com.qianxin.hiddentale.data.bean.TreeResultBean
import com.qianxin.hiddentale.data.bean.WanBannerResultBean
import com.qianxin.hiddentale.data.bean.WanHomeResultBean
import com.qianxin.hiddentale.data.bean.search.SearchHotTagResult
import io.reactivex.Observable
import retrofit2.http.*

interface WanService {

    /*
     * 导航数据
     */
    @GET("navi/json")
    fun getNaviJson(): Observable<NaviJsonBean>

    /*
     * 玩安卓首页数据，文章列表、知识体系下的文章
     */
    @GET("article/list/{page}/json")
    fun getHomeList(@Path("page") page: Int, @Query("cid") cid: Int?): Observable<WanHomeResultBean>
//    fun getHomeList(@Path("page") page: Int): Observable<WanHomeResultBean>

    /*
     * 体系数据
     */
    @GET("tree/json")
    fun getTreeJson(): Observable<TreeResultBean>

    /*
     * WanAndroid首页轮播图
     */
    @GET("banner/json")
    fun getWanAndroidBanner(): Observable<WanBannerResultBean>

    /*
     * 搜索热词
     */
    @GET("hotkey/json")
    fun getHotkey(): Observable<SearchHotTagResult>

    /*
     * wanandroid搜索
     */
    @FormUrlEncoded
    @POST("article/query/{page}/json")
    fun searchWan(@Path("page") page: Int, @Field("k") k: String): Observable<WanHomeResultBean>


}