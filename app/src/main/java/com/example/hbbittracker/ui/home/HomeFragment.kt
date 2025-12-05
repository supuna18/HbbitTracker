package com.example.hbbittracker.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hbbittracker.data.DataManager
import com.example.hbbittracker.databinding.FragmentHomeBinding
import com.example.hbbittracker.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel should be initialized lazily
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up all observers to get real-time data from the ViewModel
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data every time the fragment becomes visible to keep UI up-to-date.
        viewModel.refreshAll()
    }

    private fun setupObservers() {
        // Observe the current date from the ViewModel
        viewModel.currentDate.observe(viewLifecycleOwner) { formattedDate ->
            binding.textDate.text = formattedDate
        }

        // Observe habit progress
        viewModel.habitSummary.observe(viewLifecycleOwner) { summary ->
            binding.progressHabits.setProgress(summary.percent, true)
            binding.textHabitsPercent.text = "${summary.percent}%"
            binding.textHabitsSummary.text = summary.summaryText
            binding.textHabitsQuickSummary.text = summary.quickSummaryText
        }

        // Observe hydration progress
        viewModel.hydrationSummary.observe(viewLifecycleOwner) { summary ->
            binding.progressHydration.setProgress(summary.percent, true)
            binding.textHydrationPercent.text = "${summary.percent}%"
            binding.textWaterIntake.text = summary.intakeText
            binding.textWaterQuick.text = summary.quickIntakeText
        }

        // Observe latest mood
        viewModel.latestMood.observe(viewLifecycleOwner) { mood ->
            binding.textMoodEmoji.text = mood.emoji
            binding.textMoodLabel.text = mood.label
        }

        // Observe streak
        viewModel.streak.observe(viewLifecycleOwner) { streakText ->
            binding.textStreak.text = streakText
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding to prevent memory leaks
        _binding = null
    }
}
