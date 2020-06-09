package com.qianxin.hiddentale.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.baize.fireeyekotlin.utils.log.L
import com.qianxin.hiddentale.app.Constants
import com.qianxin.hiddentale.data.Resource
import com.qianxin.hiddentale.data.bean.GankIoDataBean
import com.qianxin.hiddentale.network.DefaultSubscriber
import com.qianxin.hiddentale.network.HttpClient
import com.qianxin.hiddentale.network.cache.ACache

//var 可变
//val 不可变
class WelfareRepository(private var network: HttpClient, private val acache: ACache) {

    //获取宝图图片
    fun getWelfareImages(start: Int, count: Int): LiveData<Resource<GankIoDataBean>> {
        L.i(msg = "请求页数：start:$start  count:$count")
        val liveData = MutableLiveData<Resource<GankIoDataBean>>()
        liveData.postValue(Resource.loading(null))
        val welfareBean = acache.getAsObject("${Constants.WELFARE_IMAGE}$start$count") as GankIoDataBean?
        if (welfareBean != null) {
            liveData.postValue(Resource.success(welfareBean))
        } else {
            getWelfareImage(start, count, liveData)
        }
        return liveData
    }

    //请求宝图图片
    private fun getWelfareImage(start: Int, count: Int, liveData: MutableLiveData<Resource<GankIoDataBean>>) {
        L.i(msg = "请求页数：type:宝图  start:$start  count:$count")
        network.getGankIoData("宝图", start, count)
                .subscribe(object : DefaultSubscriber<GankIoDataBean>() {
                    override fun _onError(errMsg: String) {
                        liveData.postValue(Resource.error(errMsg, null))
                    }

                    override fun _onNext(entity: GankIoDataBean) {
                        if (entity.results != null && !entity.results.isEmpty()) {
                            liveData.postValue(Resource.success(entity))
                            acache.put("${Constants.WELFARE_IMAGE}$start$count", entity, 24 * 60 * 60)
                        } else {
                            liveData.postValue(Resource.error("没有更多数据了~", null))
                        }
                    }
                })
    }

    companion object {
        private var instance: WelfareRepository? = null

        fun getInstantce(network: HttpClient, acache: ACache): WelfareRepository {
            if (instance == null) {
                synchronized(WelfareRepository::class.java) {
                    if (instance == null) {
                        instance = WelfareRepository(network, acache)
                    }
                }
            }
            return instance!!
        }
    }

}