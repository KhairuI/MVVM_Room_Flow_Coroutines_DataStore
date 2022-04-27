package com.example.mvvm_room_flow_coroutines_datastore.viewModel

import androidx.lifecycle.*
import com.example.mvvm_room_flow_coroutines_datastore.activity.ADD_BOOK_RESULT
import com.example.mvvm_room_flow_coroutines_datastore.activity.EDIT_BOOK_RESULT
import com.example.mvvm_room_flow_coroutines_datastore.db.BookDAO
import com.example.mvvm_room_flow_coroutines_datastore.db.PreferenceManager
import com.example.mvvm_room_flow_coroutines_datastore.db.SortOrder
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val bookDao:BookDAO,
    private val preferenceManager: PreferenceManager
):ViewModel() {

    //val searchQuery= state.getLiveData("searchQuery","")
    val searchQuery= MutableStateFlow("")
    /*val sortOrder= MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted= MutableStateFlow(false)*/

    val preferenceFlow= preferenceManager.preferenceFlow


    private val bookListEventChannel = Channel<BookListEvent>()
    val bookListEvent= bookListEventChannel.receiveAsFlow()


    private val bookFlow= combine(
        searchQuery,
        preferenceFlow
    ){ query, filterPreference ->
        Pair(query,filterPreference)
    }.flatMapLatest { (query,filterPreference)->
        bookDao.getBook(query,filterPreference.sortOrder,filterPreference.hideCompleted)
    }

    fun sortOrderSelected(sortOrder: SortOrder)= viewModelScope.launch { preferenceManager.updateSortOrder(sortOrder) }

    fun hideCompleteSelected(hideComplete:Boolean)= viewModelScope.launch { preferenceManager.updateHideCompleted(hideComplete) }

    val book= bookFlow.asLiveData()

    fun bookSelected(book:ModelBook, isImportant:Boolean)= viewModelScope.launch {
        bookListEventChannel.send(BookListEvent.NavigateToEditBook(book,isImportant))
    }

    fun bookCheckedUpdated(book:ModelBook, isChecked:Boolean)= viewModelScope.launch {
        bookDao.update(book.copy(completed = isChecked))
    }

    fun bookDelete(book:ModelBook)= viewModelScope.launch {
        bookDao.delete(book)
        bookListEventChannel.send(BookListEvent.ShowUndoDeleteTaskMessage(book))
    }

    fun undoDeleteClick(book:ModelBook)= viewModelScope.launch {
        bookDao.insert(book)
    }

    fun insertNewBook()= viewModelScope.launch {
        bookListEventChannel.send(BookListEvent.NavigateToInsertBook)
    }

    fun addOrEditResult(result: Int){
        when(result){
            ADD_BOOK_RESULT -> showBookSaveMessage("Book added")
            EDIT_BOOK_RESULT -> showBookSaveMessage("Book updated")
        }
    }

    private fun showBookSaveMessage(message: String) = viewModelScope.launch {
        bookListEventChannel.send(BookListEvent.ShowBookSaveMessage(message))
    }

    fun onDeleteAllCompleted()= viewModelScope.launch {
        bookListEventChannel.send(BookListEvent.NavigateToDeleteAllCompletedScreen)
    }

    sealed class BookListEvent {
        object NavigateToInsertBook: BookListEvent()
        object NavigateToDeleteAllCompletedScreen: BookListEvent()
        data class NavigateToEditBook(val book: ModelBook,val isImportant: Boolean) : BookListEvent()
        data class ShowUndoDeleteTaskMessage(val book: ModelBook) : BookListEvent()
        data class ShowBookSaveMessage(val message: String) : BookListEvent()
    }
}

