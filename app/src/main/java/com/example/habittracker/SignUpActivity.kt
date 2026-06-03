package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Hide action bar
        supportActionBar?.hide()

        // Initialize views
        val etName = findViewById<EditText>(R.id.et_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etPasswordConfirm = findViewById<EditText>(R.id.et_password_confirm)
        val btnSignUp = findViewById<Button>(R.id.btn_signup)
        val loginLink = findViewById<TextView>(R.id.login_link)
        val googleSignUpCard = findViewById<CardView>(R.id.google_signup_card)

        // Sign Up button click
        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etPasswordConfirm.text.toString().trim()

            if (validateForm(name, email, password, confirmPassword)) {
                // Show success toast
                showToast("Account created successfully!", false)

                // Proceed with sign up logic
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Log In link click
        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Google sign up click
        googleSignUpCard.setOnClickListener {
            showToast("Google Sign-In coming soon!", false)
        }
    }

    private fun validateForm(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            showToast("Please enter your name", true)
            return false
        }

        if (name.length < 2) {
            showToast("Name must be at least 2 characters long", true)
            return false
        }

        if (email.isEmpty()) {
            showToast("Please enter your email", true)
            return false
        }

        if (!isValidEmail(email)) {
            showToast("Please enter a valid email address", true)
            return false
        }

        if (password.isEmpty()) {
            showToast("Please enter a password", true)
            return false
        }

        if (password.length < 6) {
            showToast("Password must be at least 6 characters long", true)
            return false
        }

        if (confirmPassword.isEmpty()) {
            showToast("Please confirm your password", true)
            return false
        }

        if (password != confirmPassword) {
            showToast("Passwords do not match", true)
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showToast(message: String, isError: Boolean) {
        if (isError) {
            CustomToast.showError(this, message)
        } else {
            CustomToast.showSuccess(this, message)
        }
    }
}