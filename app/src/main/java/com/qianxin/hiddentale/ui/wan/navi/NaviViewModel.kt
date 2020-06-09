package com.qianxin.hiddentale.ui.wan.navi

import androidx.lifecycle.ViewModel
import com.qianxin.hiddentale.repository.WanRepository

class NaviViewModel(private val repository: WanRepository) : ViewModel() {

    fun getNaviJson() = repository.getNaviJsonData()

}