package com.example.mvvm_room_flow_coroutines_datastore.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.mvvm_room_flow_coroutines_datastore.R
import com.example.mvvm_room_flow_coroutines_datastore.databinding.FragmentInsertBinding
import com.example.mvvm_room_flow_coroutines_datastore.viewModel.BookInsertViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsertFragment : Fragment(R.layout.fragment_insert) {

    private lateinit var binding: FragmentInsertBinding
    private val bookInsertViewModel: BookInsertViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentInsertBinding.bind(view)

        setData()


    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        binding.apply {
            edtBook.setText(bookInsertViewModel.bookName)
            checkImportant.isChecked= bookInsertViewModel.bookImportant
            checkImportant.jumpDrawablesToCurrentState()
            txtDate.isVisible= bookInsertViewModel.book != null
            txtDate.text= "Created: ${bookInsertViewModel.book?.createFormatted}"
        }
    }
}