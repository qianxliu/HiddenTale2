package com.qianxin.hiddentale.data.bean

import com.qianxin.hiddentale.data.bean.wan.TreeBean
import java.io.Serializable

data class TreeResultBean(
        val data: List<TreeBean>,
        val errorCode: Int,
        val errorMsg: String
) : Serializable