package com.example.hbbittracker

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var rememberMe: CheckBox
    private lateinit var loginBtn: Button
    private lateinit var signupLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // UI Mapping
        emailInput = findViewById(R.id.editEmail)
        passwordInput = findViewById(R.id.editPassword)
        rememberMe = findViewById(R.id.checkRemember)
        loginBtn = findViewById(R.id.buttonLogin)
        signupLink = findViewById(R.id.textSignupLink)

        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Auto-fill if Remember Me was used
        val savedEmail = sharedPref.getString("email", "")
        val savedPassword = sharedPref.getString("password", "")
        val isRemembered = sharedPref.getBoolean("rememberMe", false)

        if (isRemembered) {
            emailInput.setText(savedEmail)
            passwordInput.setText(savedPassword)
            rememberMe.isChecked = true
        }

        // LOGIN button click
        loginBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            when {
                email.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // ✅ Simulate successful login (you can later verify with real DB)
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    // Save user details safely
                    val editor = sharedPref.edit()
                    editor.putString("email", email)
                    editor.putString("username", email.substringBefore("@")) // derive username
                    if (rememberMe.isChecked) {
                        editor.putString("password", password)
                        editor.putBoolean("rememberMe", true)
                    } else {
                        // Only remove password/flag, not profile info
                        editor.remove("password")
                        editor.putBoolean("rememberMe", false)
                    }
                    editor.apply()

                    // Go to HomeActivity
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            }
        }

        // Signup Link
        signupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
