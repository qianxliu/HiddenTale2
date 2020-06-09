package com.qianxin.hiddentale.ui.wan.tree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qianxin.hiddentale.repository.WanRepository

class TreeViewModelFactory(private val repository: WanRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TreeViewModel(repository) as T
    }

}