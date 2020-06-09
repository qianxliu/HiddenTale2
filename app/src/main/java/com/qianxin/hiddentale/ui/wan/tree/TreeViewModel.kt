package com.qianxin.hiddentale.ui.wan.tree

import androidx.lifecycle.ViewModel
import com.qianxin.hiddentale.repository.WanRepository

class TreeViewModel(private val repository: WanRepository) : ViewModel() {

    fun getTreeJson() = repository.getTreeJson()

}