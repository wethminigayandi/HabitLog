package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "LoginPrefs"
    private val EMAIL_KEY = "saved_email"
    private val PASSWORD_KEY = "saved_password"
    private val REMEMBER_KEY = "remember_me"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Hide action bar
        supportActionBar?.hide()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Initialize views
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val checkboxRemember = findViewById<CheckBox>(R.id.checkbox_remember)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val signupLink = findViewById<TextView>(R.id.signup_link)
        val forgotPassword = findViewById<TextView>(R.id.forgot_password)
        val googleLoginCard = findViewById<CardView>(R.id.google_login_card)

        // Load saved credentials if "Remember me" was checked
        loadSavedCredentials(etEmail, etPassword, checkboxRemember)

        // Login button click
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val rememberMe = checkboxRemember.isChecked

            if (validateForm(email, password)) {
                // Save credentials if remember me is checked
                if (rememberMe) {
                    saveCredentials(email, password, true)
                } else {
                    clearSavedCredentials()
                }

                // Perform login
                performLogin(email, password)
            }
        }

        // Sign Up link click
        signupLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Forgot Password click
        forgotPassword.setOnClickListener {
            showToast("Forgot Password feature coming soon!", false)
        }

        // Google login click
        googleLoginCard.setOnClickListener {
            showToast("Google Sign-In coming soon!", false)
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            showToast("Please enter your email", true)
            return false
        }

        if (!isValidEmail(email)) {
            showToast("Please enter a valid email address", true)
            return false
        }

        if (password.isEmpty()) {
            showToast("Please enter your password", true)
            return false
        }

        if (password.length < 6) {
            showToast("Password must be at least 6 characters long", true)
            return false
        }
        return true
    }

    private fun performLogin(email: String, password: String) {
        if (email == "wg@gmail.com" && password == "pass123") {
            showToast("Login successful!", false)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            showToast("Invalid email or password", true)
        }
    }

    private fun saveCredentials(email: String, password: String, remember: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putString(EMAIL_KEY, email)
        editor.putString(PASSWORD_KEY, password)
        editor.putBoolean(REMEMBER_KEY, remember)
        editor.apply()
    }

    private fun loadSavedCredentials(etEmail: EditText, etPassword: EditText, checkboxRemember: CheckBox) {
        val savedEmail = sharedPreferences.getString(EMAIL_KEY, "")
        val savedPassword = sharedPreferences.getString(PASSWORD_KEY, "")
        val rememberMe = sharedPreferences.getBoolean(REMEMBER_KEY, false)

        if (rememberMe && !savedEmail.isNullOrEmpty()) {
            etEmail.setText(savedEmail)
            etPassword.setText(savedPassword)
            checkboxRemember.isChecked = true
        }
    }

    private fun clearSavedCredentials() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showToast(message: String, isError: Boolean) {
        if (isError) {
            Toast.makeText(this, "❌ $message", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "✅ $message", Toast.LENGTH_SHORT).show()
        }
    }
}