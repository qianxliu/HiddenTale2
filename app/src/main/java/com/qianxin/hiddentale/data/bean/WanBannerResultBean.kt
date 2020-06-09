package com.qianxin.hiddentale.data.bean

import com.qianxin.hiddentale.data.bean.wan.WanBannerBean
import java.io.Serializable

data class WanBannerResultBean(
        val data: List<WanBannerBean>,
        val errorCode: Int,
        val errorMsg: String
) : Serializable