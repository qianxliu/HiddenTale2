package com.qianxin.hiddentale.ui.wan.article

import androidx.lifecycle.ViewModel
import com.qianxin.hiddentale.repository.WanRepository

class ArticleViewModel(private val repository: WanRepository) : ViewModel() {

    var page = 0
    var cid: Int? = null

    fun getArticleList() = repository.getWanHomeData(page, cid)

    fun setCid(cid: Int) {
        if (cid == 0) {
            this.cid = null
        } else {
            this.cid = cid
        }
    }

    fun reset() {
        page = 0
    }

    fun handleNextPage() {
        page += 1
    }

}