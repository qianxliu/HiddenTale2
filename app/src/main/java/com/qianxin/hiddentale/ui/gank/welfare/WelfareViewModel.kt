package com.qianxin.hiddentale.ui.gank.welfare

import androidx.lifecycle.ViewModel
import com.qianxin.hiddentale.app.Constants
import com.qianxin.hiddentale.data.bean.gankchild.GankResult
import com.qianxin.hiddentale.repository.WelfareRepository

class WelfareViewModel(private val repository: WelfareRepository) : ViewModel() {

    var page = 1

    var welfareResult = ArrayList<GankResult>()

    //获取宝图图片
    fun getWelfareImage() = repository.getWelfareImages(page, Constants.PAGE_COUNT)

    fun handleNextPage() {
        page += 1
    }

}