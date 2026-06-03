package com.example.habittracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class LogoutConfirmationDialogFragment(
    private val onConfirm: (Boolean) -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_logout_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvMessage = view.findViewById<TextView>(R.id.tv_message)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        tvMessage.text = "Are you sure you want to logout?"

        btnLogout.setOnClickListener {
            onConfirm(true)
            dismiss()
        }

        btnCancel.setOnClickListener {
            onConfirm(false)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
