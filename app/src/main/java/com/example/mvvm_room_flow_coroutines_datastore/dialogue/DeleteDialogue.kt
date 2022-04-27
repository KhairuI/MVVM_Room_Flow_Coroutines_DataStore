package com.example.mvvm_room_flow_coroutines_datastore.dialogue

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.mvvm_room_flow_coroutines_datastore.viewModel.DeleteAllViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteDialogue: DialogFragment() {

    private val deleteViewModel: DeleteAllViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext()).setTitle("Confirmation Delete")
            .setMessage("Do you want to delete all completed book?")
            .setNegativeButton("NO",null)
            .setPositiveButton("YES"){ _,_->
                deleteViewModel.onConfirmClick()
            }.create()
}