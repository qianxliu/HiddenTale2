package com.qianxin.hiddentale.data.bean

import com.qianxin.hiddentale.data.bean.gankchild.GankResult
import java.io.Serializable

data class GankIoDataBean(
        val error: Boolean,
        val results: List<GankResult>?
) : Serializable