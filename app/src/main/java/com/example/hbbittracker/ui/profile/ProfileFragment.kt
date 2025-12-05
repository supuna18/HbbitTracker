package com.example.hbbittracker.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.Fragment
import com.example.hbbittracker.LoginActivity
import com.example.hbbittracker.R

class ProfileFragment : Fragment() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.etEmail)
        btnSave = view.findViewById(R.id.btnSave)
        btnLogout = view.findViewById(R.id.btnLogout)

        loadUserData()

        btnSave.setOnClickListener { saveUserData() }
        btnLogout.setOnClickListener { logoutUser() }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadUserData() // Refresh when coming back
    }

    private fun loadUserData() {
        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Guest")
        val email = prefs.getString("email", "guest@example.com")

        etUsername.setText(username)
        etEmail.setText(email)
    }

    private fun saveUserData() {
        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("username", etUsername.text.toString())
        editor.putString("email", etEmail.text.toString())
        editor.apply()
    }

    private fun logoutUser() {
        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
