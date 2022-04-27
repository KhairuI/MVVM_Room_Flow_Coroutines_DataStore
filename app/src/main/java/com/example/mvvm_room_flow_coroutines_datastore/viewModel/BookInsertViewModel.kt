package com.example.mvvm_room_flow_coroutines_datastore.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm_room_flow_coroutines_datastore.activity.ADD_BOOK_RESULT
import com.example.mvvm_room_flow_coroutines_datastore.activity.EDIT_BOOK_RESULT
import com.example.mvvm_room_flow_coroutines_datastore.db.BookDAO
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookInsertViewModel @Inject constructor(
    private val bookDao:BookDAO,
): ViewModel(){

    /*val book= state.get<ModelBook>("book")

    var bookName= state.get<String>("bookName")?: book?.name ?: ""
    set(value){
        field= value
        state.set("bookName",value)
    }

    var bookImportant= state.get<Boolean>("bookImportant")?: book?.important ?: false
        set(value){
            field= value
            state.set("bookImportant",value)
        }*/

    private val bookInsertEventChannel = Channel<BookInsertEvent>()
    val bokInsertEvent= bookInsertEventChannel.receiveAsFlow()

    fun onSaveClick(book: ModelBook, isInsert:Boolean){

        if(book.name.isBlank()) {
            showInvalidInputMessage("Name can not be empty")
           return
        }
        if(isInsert) insertBook(book)
        else updateBook(book)
    }

    private fun updateBook(book: ModelBook) = viewModelScope.launch {
        bookDao.update(book)
        bookInsertEventChannel.send(BookInsertEvent.NavigateBackWithResult(EDIT_BOOK_RESULT))
    }

    private fun insertBook(book: ModelBook)= viewModelScope.launch {
        bookDao.insert(book)
        bookInsertEventChannel.send(BookInsertEvent.NavigateBackWithResult(ADD_BOOK_RESULT))
    }

    private fun showInvalidInputMessage(message:String)= viewModelScope.launch {
        bookInsertEventChannel.send(BookInsertEvent.ShowInvalidInputMessage(message))
    }

    sealed class BookInsertEvent {
        data class ShowInvalidInputMessage(val message:String) : BookInsertEvent()
        data class NavigateBackWithResult(val result:Int) : BookInsertEvent()
    }

}