package com.example.mvvm_room_flow_coroutines_datastore.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_room_flow_coroutines_datastore.R
import com.example.mvvm_room_flow_coroutines_datastore.adapter.BookAdapter
import com.example.mvvm_room_flow_coroutines_datastore.databinding.FragmentBookListBinding
import com.example.mvvm_room_flow_coroutines_datastore.db.SortOrder
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook
import com.example.mvvm_room_flow_coroutines_datastore.utils.onQueryTextChanged
import com.example.mvvm_room_flow_coroutines_datastore.viewModel.BookListViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookListFragment : Fragment(R.layout.fragment_book_list) {

    private lateinit var binding: FragmentBookListBinding
    private val bookListViewModel: BookListViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentBookListBinding.bind(view)

        // all observe method
        observeBookList()
        observeTaskEvents()

        // All click
        clickEvents()

        setHasOptionsMenu(true)

    }

    private fun clickEvents() {
        binding.btnAdd.setOnClickListener {
            bookListViewModel.insertNewBook()
        }
    }

    @SuppressLint("ShowToast")
    private fun observeTaskEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            bookListViewModel.taskEvent.collect { event->
                when(event){
                    is BookListViewModel.TasksEvent.ShowUndoDeleteTaskMessage ->{
                        Snackbar.make(requireView(),"Book deleted", Snackbar.LENGTH_LONG).setAction("UNDO"){
                            bookListViewModel.undoDeleteClick(event.book)
                        }.show()
                    }
                }
            }
        }
    }

    private fun observeBookList() {
        bookListViewModel.book.observe(viewLifecycleOwner){
            setData(it)
        }
    }

    private fun setData(bookList: MutableList<ModelBook>?) {
        bookAdapter= BookAdapter(object : BookAdapter.OnItemClickListener{
            override fun onItemClick(book: ModelBook) {
                bookListViewModel.bookSelected(book)
            }

            override fun onCheckBoxClick(book: ModelBook, isChecked: Boolean) {
                bookListViewModel.bookCheckedUpdated(book,isChecked)
            }

        })
        binding.rvBookList.apply {
            setHasFixedSize(true)
            adapter= bookAdapter
        }
        bookAdapter.submitList(bookList)

        // set item swipe

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val book= bookAdapter.currentList[viewHolder.adapterPosition]
                bookListViewModel.bookDelete(book)
            }

        }).attachToRecyclerView(binding.rvBookList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item,menu)

        val searchItem= menu.findItem(R.id.action_search)
        val searchView= searchItem.actionView as SearchView
        searchView.onQueryTextChanged {
            bookListViewModel.searchQuery.value= it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked=
                bookListViewModel.preferenceFlow.first().hideCompleted

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_sort_by_name -> {
                bookListViewModel.sortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created -> {
                bookListViewModel.sortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                bookListViewModel.hideCompleteSelected(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}