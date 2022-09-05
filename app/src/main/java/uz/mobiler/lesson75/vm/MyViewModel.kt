package uz.mobiler.lesson75.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.mobiler.lesson75.paging3.ImageDataPagingDataSource

class MyViewModel(private val query: String, private val bol: Boolean) : ViewModel() {

    val flow = Pager(
        PagingConfig(20)
    ) {
        ImageDataPagingDataSource(query, bol)
    }.flow.cachedIn(viewModelScope)
}