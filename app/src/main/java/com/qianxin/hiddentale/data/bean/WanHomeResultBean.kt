package com.qianxin.hiddentale.data.bean

import com.qianxin.hiddentale.data.bean.wan.WanHomeBean
import java.io.Serializable

data class WanHomeResultBean(
        val data: WanHomeBean,
        val errorCode: Int,
        val errorMsg: String
) : Serializable