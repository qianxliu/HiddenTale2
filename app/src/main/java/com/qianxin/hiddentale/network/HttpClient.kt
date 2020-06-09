package com.qianxin.hiddentale.network

import com.qianxin.hiddentale.data.bean.GankIoDataBean
import com.qianxin.hiddentale.network.service.GankService
import com.qianxin.hiddentale.network.service.WanService
import io.reactivex.Observable

class HttpClient : BaseReqo() {

    private fun getGankService(): GankService = ServiceCreate.create(GankService::class.java, ServiceCreate.API_GANKIO)
    private fun getWanService(): WanService = ServiceCreate.create(WanService::class.java, ServiceCreate.API_WAN_ANDROID)


    //==============================干货集中营===============================
    fun getGankIoData(type: String, page: Int, pre_page: Int): Observable<GankIoDataBean> = transform(getGankService().getGankIoData(type, page, pre_page))

    fun searchGank(page: Int, type: String, keyword: String) = transform(getGankService().searchGank(page, type, keyword))
    //==============================干货集中营end===============================

    //==============================玩安卓API===============================
    fun getNaviJson() = transform(getWanService().getNaviJson())

    fun getTreeJson() = transform(getWanService().getTreeJson())

    fun getWanHome(page: Int, cid: Int?) = transform(getWanService().getHomeList(page, cid))

    fun getWanAndroidBanner() = transform(getWanService().getWanAndroidBanner())

    fun getHotkey() = transform(getWanService().getHotkey())

    fun searchWan(page: Int, keyword: String) = transform(getWanService().searchWan(page, keyword))

    //==============================玩安卓end===============================


    companion object {
        private var network: HttpClient? = null

        fun getInstance(): HttpClient {
            if (network == null) {
                synchronized(HttpClient::class.java) {
                    if (network == null) {
                        network = HttpClient()
                    }
                }
            }
            return network!!
        }
    }
}