package com.qianxin.hiddentale.data.bean.search

import java.io.Serializable

data class SearchHotTagBean(
        val id: Int,
        val link: String,
        val name: String,
        val order: Int,
        val visible: Int
) : Serializable