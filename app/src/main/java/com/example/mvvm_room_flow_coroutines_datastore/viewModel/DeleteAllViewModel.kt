package com.example.mvvm_room_flow_coroutines_datastore.viewModel

import androidx.lifecycle.ViewModel
import com.example.mvvm_room_flow_coroutines_datastore.db.BookDAO
import com.example.mvvm_room_flow_coroutines_datastore.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllViewModel @Inject constructor(
    private val bookDao: BookDAO,
    @ApplicationScope private val applicationScope: CoroutineScope
) :ViewModel() {

    fun onConfirmClick()= applicationScope.launch {
        bookDao.deleteAllCompletedBook()
    }
}