package com.example.mvvm_room_flow_coroutines_datastore.viewModel

import androidx.lifecycle.*
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
    private val preferenceManager: PreferenceManager,
    private val state: SavedStateHandle
):ViewModel() {

    val searchQuery= state.getLiveData("searchQuery","")
    //val searchQuery= MutableStateFlow("")
    /*val sortOrder= MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted= MutableStateFlow(false)*/

    val preferenceFlow= preferenceManager.preferenceFlow


    private val tasksEventChannel = Channel<TasksEvent>()
    val taskEvent= tasksEventChannel.receiveAsFlow()


    private val bookFlow= combine(
        searchQuery.asFlow(),
        preferenceFlow
    ){ query, filterPreference ->
        Pair(query,filterPreference)
    }.flatMapLatest { (query,filterPreference)->
        bookDao.getBook(query,filterPreference.sortOrder,filterPreference.hideCompleted)
    }

    fun sortOrderSelected(sortOrder: SortOrder)= viewModelScope.launch { preferenceManager.updateSortOrder(sortOrder) }

    fun hideCompleteSelected(hideComplete:Boolean)= viewModelScope.launch { preferenceManager.updateHideCompleted(hideComplete) }

    val book= bookFlow.asLiveData()

    fun bookSelected(book:ModelBook)= viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditBook(book))
    }

    fun bookCheckedUpdated(book:ModelBook, isChecked:Boolean)= viewModelScope.launch {
        bookDao.update(book.copy(completed = isChecked))
    }

    fun bookDelete(book:ModelBook)= viewModelScope.launch {
        bookDao.delete(book)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(book))
    }

    fun undoDeleteClick(book:ModelBook)= viewModelScope.launch {
        bookDao.insert(book)
    }

    fun insertNewBook()= viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToInsertBook)
    }

    sealed class TasksEvent {
        object NavigateToInsertBook: TasksEvent()
        data class NavigateToEditBook(val book: ModelBook) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val book: ModelBook) : TasksEvent()
    }
}

