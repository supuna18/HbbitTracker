package com.example.hbbittracker.ui.hydration

import android.Manifest // Required for permission constants
import android.content.pm.PackageManager // Required for checking permissions
import android.os.Build // Required for version checks
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts // Required for the new permission model
import androidx.core.content.ContextCompat // Required for checking permissions
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hbbittracker.R
import com.example.hbbittracker.data.DataManager
import com.example.hbbittracker.reminders.HydrationScheduler
import com.example.hbbittracker.viewmodel.DashboardViewModel

class HydrationFragment : Fragment() {

    // --- Views ---
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPercent: TextView
    private lateinit var tvCurrent: TextView
    private lateinit var tvAmount: TextView
    private lateinit var btnAddWater: Button
    private lateinit var btnReset: Button
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private lateinit var spinnerInterval: Spinner
    private lateinit var switchReminders: Switch

    // --- State variables ---
    private var amountToAdd = 250
    private var selectedInterval = 30
    private lateinit var dataManager: DataManager
    private lateinit var viewModel: DashboardViewModel

    // ✅ ADDED: ActivityResultLauncher for handling the notification permission request.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission was granted by the user.
                scheduleReminder()
                Toast.makeText(requireContext(), "Reminders enabled!", Toast.LENGTH_SHORT).show()
            } else {
                // Permission was denied. Inform the user and revert the switch state.
                Toast.makeText(requireContext(), "Notification permission denied. Reminders cannot be set.", Toast.LENGTH_LONG).show()
                switchReminders.isChecked = false
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_hydration, container, false)
        dataManager = DataManager(requireContext())
        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]

        // Initialize all views
        progressBar = v.findViewById(R.id.progressBar)
        tvPercent = v.findViewById(R.id.tvPercent)
        tvCurrent = v.findViewById(R.id.tvCurrent)
        tvAmount = v.findViewById(R.id.tvAmount)
        btnAddWater = v.findViewById(R.id.btnAddWater)
        btnReset = v.findViewById(R.id.btnReset)
        btnPlus = v.findViewById(R.id.btnPlus)
        btnMinus = v.findViewById(R.id.btnMinus)
        spinnerInterval = v.findViewById(R.id.spinnerInterval)
        switchReminders = v.findViewById(R.id.switchReminders)

        setupSpinner()
        setupListeners()
        updateProgress()

        return v
    }

    private fun setupListeners() {
        btnPlus.setOnClickListener {
            amountToAdd += 50
            tvAmount.text = "${amountToAdd}ml"
        }

        btnMinus.setOnClickListener {
            if (amountToAdd > 50) amountToAdd -= 50
            tvAmount.text = "${amountToAdd}ml"
        }

        btnAddWater.setOnClickListener {
            dataManager.addWater(amountToAdd)
            updateProgress()
            Toast.makeText(requireContext(), "💧 Added $amountToAdd ml", Toast.LENGTH_SHORT).show()
            viewModel.refreshAll() // Notify dashboard to refresh
        }

        btnReset.setOnClickListener {
            dataManager.resetWater()
            updateProgress()
            Toast.makeText(requireContext(), "Progress reset", Toast.LENGTH_SHORT).show()
            viewModel.refreshAll()
        }

        // ✅ UPDATED: The switch listener now triggers the permission check flow.
        switchReminders.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // When the user toggles ON, start the permission check.
                checkAndRequestNotificationPermission()
            } else {
                // When the user toggles OFF, simply cancel the reminder.
                HydrationScheduler.cancelReminder(requireContext())
                Toast.makeText(requireContext(), "Reminders disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ ADDED: New function to handle the permission logic.
    private fun checkAndRequestNotificationPermission() {
        // Notification permission is only required on Android 13 (TIRAMISU) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted, so we can schedule the reminder directly.
                    scheduleReminder()
                }
                else -> {
                    // Permission has not been granted, so we launch the request dialog.
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For older Android versions (below 13), no permission is needed.
            scheduleReminder()
        }
    }

    private fun scheduleReminder() {
        HydrationScheduler.scheduleReminder(requireContext(), selectedInterval)
    }

    private fun setupSpinner() {
        val intervals = arrayOf("30", "60", "120") // Example intervals
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, intervals)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInterval.adapter = adapter
        spinnerInterval.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedInterval = intervals[position].toInt()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateProgress() {
        val percent = dataManager.getHydrationProgressPercent()
        val dailyGoal = dataManager.getDailyWaterGoal()
        val currentIntake = dataManager.getWaterToday()

        progressBar.progress = percent
        tvPercent.text = "$percent%"
        tvCurrent.text = "Current: $currentIntake / $dailyGoal ml"
    }
}