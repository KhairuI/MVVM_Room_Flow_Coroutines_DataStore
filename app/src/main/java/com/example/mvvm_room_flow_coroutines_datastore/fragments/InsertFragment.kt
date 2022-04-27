package com.example.mvvm_room_flow_coroutines_datastore.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mvvm_room_flow_coroutines_datastore.R
import com.example.mvvm_room_flow_coroutines_datastore.databinding.FragmentInsertBinding
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook
import com.example.mvvm_room_flow_coroutines_datastore.utils.exhaustive
import com.example.mvvm_room_flow_coroutines_datastore.viewModel.BookInsertViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class InsertFragment : Fragment(R.layout.fragment_insert) {

    private lateinit var binding: FragmentInsertBinding
    private val bookInsertViewModel: BookInsertViewModel by viewModels()
    private val args: InsertFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentInsertBinding.bind(view)

        if(args.book!=null) setData()

        // all observe method
        observeBookInsertEvents()

        // click Events
        clickEvent()


    }

    private fun observeBookInsertEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            bookInsertViewModel.bokInsertEvent.collect { event->
                when(event){
                    is BookInsertViewModel.BookInsertEvent.NavigateBackWithResult -> {
                        binding.edtBook.clearFocus()
                        setFragmentResult("add_edit_request", bundleOf("add_edit_result" to event.result))
                        findNavController().popBackStack()
                    }
                    is BookInsertViewModel.BookInsertEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(),event.message, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

    private fun clickEvent() {
        binding.apply {

            btnSave.setOnClickListener {
                if(args.book==null){
                    val book= ModelBook(name = edtBook.text.toString(), important = checkImportant.isChecked)
                    bookInsertViewModel.onSaveClick(book, true)
                }
                else{
                    val book= args.book!!.copy(name = edtBook.text.toString(), important = checkImportant.isChecked)
                    bookInsertViewModel.onSaveClick(book, false)
                }
               /* val name= edtBook.text.toString()
                val isImportant= checkImportant.isChecked
                val isCompleted= if(args.book!=null) args.book!!.completed else false
                val isInsert= args.book==null
                bookInsertViewModel.onSaveClick(name, isImportant, isCompleted, isInsert)*/
            }


        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        binding.apply {
            val book= args.book
            edtBook.setText(book?.name)
            checkImportant.isChecked= book?.important!!
            txtDate.text= "Created: ${book.createFormatted}"
        }
    }
}