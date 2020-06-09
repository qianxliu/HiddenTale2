package com.qianxin.hiddentale.data.bean

import com.qianxin.hiddentale.data.bean.wan.NaviBean
import java.io.Serializable

data class NaviJsonBean(
        val data: List<NaviBean>,
        val errorCode: Int,
        val errorMsg: String) : Serializable {
}