package com.example.mvvm_room_flow_coroutines_datastore.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_room_flow_coroutines_datastore.R
import com.example.mvvm_room_flow_coroutines_datastore.adapter.BookAdapter
import com.example.mvvm_room_flow_coroutines_datastore.databinding.FragmentBookListBinding
import com.example.mvvm_room_flow_coroutines_datastore.db.SortOrder
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook
import com.example.mvvm_room_flow_coroutines_datastore.utils.exhaustive
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
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentBookListBinding.bind(view)

        // all observe method
        observeBookList()
        observeBookListEvents()

        // All click
        clickEvents()

        // set fragment result listener
        setFragmentResultListener("add_edit_request"){ _,bundle->
            val result= bundle.getInt("add_edit_result")
            bookListViewModel.addOrEditResult(result)
        }

        setHasOptionsMenu(true)

    }

    private fun clickEvents() {
        binding.btnAdd.setOnClickListener {
            bookListViewModel.insertNewBook()
        }
    }

    @SuppressLint("ShowToast")
    private fun observeBookListEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            bookListViewModel.bookListEvent.collect { event->
                when(event){
                    is BookListViewModel.BookListEvent.ShowUndoDeleteTaskMessage ->{
                        Snackbar.make(requireView(),"Book deleted", Snackbar.LENGTH_LONG).setAction("UNDO"){
                            bookListViewModel.undoDeleteClick(event.book)
                        }.show()
                    }
                    is BookListViewModel.BookListEvent.NavigateToEditBook -> {
                        val action= BookListFragmentDirections.actionBookListFragmentToInsertFragment(event.book.copy(important = event.isImportant),"Edit Book")
                        findNavController().navigate(action)
                    }
                    is BookListViewModel.BookListEvent.NavigateToInsertBook -> {
                        val action= BookListFragmentDirections.actionBookListFragmentToInsertFragment(null,"Add Book")
                        findNavController().navigate(action)
                    }
                    is BookListViewModel.BookListEvent.ShowBookSaveMessage -> {
                        Snackbar.make(requireView(),event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    BookListViewModel.BookListEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action= BookListFragmentDirections.actionGlobalDeleteDialogue()
                        findNavController().navigate(action)
                    }
                }.exhaustive
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
            override fun onItemClick(book: ModelBook, isImportant: Boolean) {
                bookListViewModel.bookSelected(book,isImportant)
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
        searchView= searchItem.actionView as SearchView

        val pendingQuery= bookListViewModel.searchQuery.value
        if(pendingQuery.isNotEmpty()){
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

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
                bookListViewModel.onDeleteAllCompleted()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }

}