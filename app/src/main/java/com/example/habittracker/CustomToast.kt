package com.example.habittracker

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

class CustomToast {
    companion object {
        fun showError(context: Context, message: String) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            val view = toast.view
            view?.setBackgroundColor(ContextCompat.getColor(context, R.color.error_red))
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
            toast.show()
        }

        fun showSuccess(context: Context, message: String) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            val view = toast.view
            view?.setBackgroundColor(ContextCompat.getColor(context, R.color.success_green))
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
            toast.show()
        }

        fun showInfo(context: Context, message: String) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            val view = toast.view
            view?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary))
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
            toast.show()
        }
    }
}