package com.example.hbbittracker

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var policyCheck: CheckBox
    private lateinit var signupBtn: Button
    private lateinit var loginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // UI Mapping
        emailInput = findViewById(R.id.editEmail)
        passwordInput = findViewById(R.id.editPassword)
        confirmPasswordInput = findViewById(R.id.editConfirmPassword)
        policyCheck = findViewById(R.id.checkPolicy)
        signupBtn = findViewById(R.id.buttonSignup)
        loginLink = findViewById(R.id.textLoginLink)

        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        signupBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()
            val agreed = policyCheck.isChecked

            when {
                email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                !agreed -> {
                    Toast.makeText(this, "Please agree to the Privacy Policy", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // ✅ Save new user details
                    val editor = sharedPref.edit()
                    editor.putString("email", email)
                    editor.putString("username", email.substringBefore("@"))
                    editor.putString("password", password)
                    editor.putBoolean("rememberMe", true)
                    editor.apply()

                    Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()

                    // Redirect to Login
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
