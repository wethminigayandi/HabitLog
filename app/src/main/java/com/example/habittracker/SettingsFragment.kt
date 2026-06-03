package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class SettingsFragment : Fragment() {

    private lateinit var llAccount: LinearLayout
    private lateinit var llTermsConditions: LinearLayout
    private lateinit var llPolicy: LinearLayout
    private lateinit var llAboutApp: LinearLayout
    private lateinit var btnLogout: MaterialButton

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initSharedPreferences()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        llAccount = view.findViewById(R.id.ll_account)
        llTermsConditions = view.findViewById(R.id.ll_terms_conditions)
        llPolicy = view.findViewById(R.id.ll_policy)
        llAboutApp = view.findViewById(R.id.ll_about_app)
        btnLogout = view.findViewById(R.id.btn_logout)
    }

    private fun initSharedPreferences() {
        sharedPreferences = requireActivity().getSharedPreferences(
            "HabitTrackerPrefs",
            Context.MODE_PRIVATE
        )
    }

    private fun setupClickListeners() {
        llAccount.setOnClickListener {
            // Handle account click
            Toast.makeText(context, "Account settings", Toast.LENGTH_SHORT).show()
        }

        llTermsConditions.setOnClickListener {
            // Handle terms and conditions click
            Toast.makeText(context, "Terms and Conditions", Toast.LENGTH_SHORT).show()
        }

        llPolicy.setOnClickListener {
            // Handle policy click
            Toast.makeText(context, "Policy", Toast.LENGTH_SHORT).show()
        }

        llAboutApp.setOnClickListener {
            // Handle about app click
            Toast.makeText(context, "About App", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        val dialog = LogoutConfirmationDialogFragment { confirmed ->
            if (confirmed) {
                performLogout()
            }
        }
        dialog.show(parentFragmentManager, "LogoutConfirmationDialog")
    }

    private fun performLogout() {
        // Clear all user session data
        clearUserSession()

        // Clear all app data (habits, goals, mood entries)
        clearAppData()

        // Navigate to LoginActivity and finish current activity
        navigateToLogin()
    }

    private fun clearUserSession() {
        val editor = sharedPreferences.edit()

        // Clear login credentials
        editor.remove("isLoggedIn")
        editor.remove("username")
        editor.remove("password")
        editor.remove("userEmail")
        editor.remove("firstName")
        editor.remove("lastName")

        // Clear any other user-specific data
        editor.remove("loginTimestamp")
        editor.remove("rememberMe")

        editor.apply()
    }

    private fun clearAppData() {
        // Clear habits and goals data from HomeFragment
        HomeFragment.todayHabits.clear()
        HomeFragment.goals.clear()

        // You can also clear data from SharedPreferences if you store any app data there
        val editor = sharedPreferences.edit()
        editor.remove("habitsData")
        editor.remove("goalsData")
        editor.remove("moodData")
        editor.apply()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)

        // Clear the activity stack and start fresh
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(intent)

        // Finish the current activity (MainActivity)
        requireActivity().finish()

        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}
