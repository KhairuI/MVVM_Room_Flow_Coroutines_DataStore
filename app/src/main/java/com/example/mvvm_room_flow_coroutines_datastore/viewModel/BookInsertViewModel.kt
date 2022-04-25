package com.example.mvvm_room_flow_coroutines_datastore.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.mvvm_room_flow_coroutines_datastore.db.BookDAO
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookInsertViewModel @Inject constructor(
    private val bookDao:BookDAO,
    @Assisted private val state:SavedStateHandle
): ViewModel(){

    val book= state.get<ModelBook>("book")

    var bookName= state.get<String>("bookName")?: book?.name ?: ""
    set(value){
        field= value
        state.set("bookName",value)
    }

    var bookImportant= state.get<Boolean>("bookImportant")?: book?.important ?: false
        set(value){
            field= value
            state.set("bookImportant",value)
        }

}