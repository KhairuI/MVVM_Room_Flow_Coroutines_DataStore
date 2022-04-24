package com.example.mvvm_room_flow_coroutines_datastore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.mvvm_room_flow_coroutines_datastore.R
import com.example.mvvm_room_flow_coroutines_datastore.databinding.FragmentInsertBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsertFragment : Fragment(R.layout.fragment_insert) {

    private lateinit var binding: FragmentInsertBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentInsertBinding.bind(view)
    }
}