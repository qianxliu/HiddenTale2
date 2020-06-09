package com.qianxin.hiddentale.data.bean.search

import java.io.Serializable

data class SearchHotTagResult(
        val data: List<SearchHotTagBean>,
        val errorCode: Int,
        val errorMsg: String
) : Serializable