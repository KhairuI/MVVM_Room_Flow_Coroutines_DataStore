package com.example.mvvm_room_flow_coroutines_datastore.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mvvm_room_flow_coroutines_datastore.db.BookDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookDao:BookDAO
):ViewModel() {

    val searchQuery= MutableStateFlow("")
    val sortOrder= MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted= MutableStateFlow(false)

    private val bookFlow= combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ){ query, sortOrder, hideCompleted ->
        Triple(query,sortOrder,hideCompleted)
    }.flatMapLatest { (query,sortOrder,hideCompleted)->
        bookDao.getBook(query,sortOrder,hideCompleted)
    }

    val book= bookFlow.asLiveData()
}

enum class SortOrder{ BY_NAME, BY_DATE}