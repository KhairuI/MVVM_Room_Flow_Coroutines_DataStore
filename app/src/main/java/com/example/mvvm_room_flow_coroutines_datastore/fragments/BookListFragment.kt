package com.example.mvvm_room_flow_coroutines_datastore.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.example.mvvm_room_flow_coroutines_datastore.R
import com.example.mvvm_room_flow_coroutines_datastore.adapter.BookAdapter
import com.example.mvvm_room_flow_coroutines_datastore.databinding.FragmentBookListBinding
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook
import com.example.mvvm_room_flow_coroutines_datastore.utils.onQueryTextChanged
import com.example.mvvm_room_flow_coroutines_datastore.viewModel.BookViewModel
import com.example.mvvm_room_flow_coroutines_datastore.viewModel.SortOrder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookListFragment : Fragment(R.layout.fragment_book_list) {

    private lateinit var binding: FragmentBookListBinding
    private val bookViewModel: BookViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentBookListBinding.bind(view)

        // all observe method
        observeBookList()

        setHasOptionsMenu(true)

    }

    private fun observeBookList() {
        bookViewModel.book.observe(viewLifecycleOwner){
            setData(it)
        }
    }

    private fun setData(bookList: MutableList<ModelBook>?) {
        bookAdapter= BookAdapter()
        binding.rvBookList.apply {
            setHasFixedSize(true)
            adapter= bookAdapter
        }
        bookAdapter.submitList(bookList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item,menu)

        val searchItem= menu.findItem(R.id.action_search)
        val searchView= searchItem.actionView as SearchView
        searchView.onQueryTextChanged {
            bookViewModel.searchQuery.value= it
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_sort_by_name -> {
                bookViewModel.sortOrder.value= SortOrder.BY_NAME
                true
            }
            R.id.action_sort_by_date_created -> {
                bookViewModel.sortOrder.value= SortOrder.BY_DATE
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                bookViewModel.hideCompleted.value= item.isChecked
                true
            }
            R.id.action_delete_all_completed_tasks -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}