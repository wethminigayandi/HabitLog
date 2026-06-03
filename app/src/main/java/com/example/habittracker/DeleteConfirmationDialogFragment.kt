package com.example.habittracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class DeleteConfirmationDialogFragment(
    private val onConfirm: (Boolean) -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_delete_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_delete).setOnClickListener {
            onConfirm(true)
            dismiss()
        }

        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            onConfirm(false)
            dismiss()
        }
    }
}
