package com.example.hbbittracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hbbittracker.data.DataManager
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel : ViewModel() {

    // 🟢 DataManager instance
    private val dataManager = DataManager()

    // 🟢 Private MutableLiveData
    private val _currentDate = MutableLiveData<String>()
    private val _habitSummary = MutableLiveData<HabitSummary>()
    private val _hydrationSummary = MutableLiveData<HydrationSummary>()
    private val _latestMood = MutableLiveData<MoodData>()
    private val _streak = MutableLiveData<String>()

    // 🟢 Public LiveData (Observed by UI)
    val currentDate: LiveData<String> get() = _currentDate
    val habitSummary: LiveData<HabitSummary> get() = _habitSummary
    val hydrationSummary: LiveData<HydrationSummary> get() = _hydrationSummary
    val latestMood: LiveData<MoodData> get() = _latestMood
    val streak: LiveData<String> get() = _streak

    // -------------------------------------------------------
    // 🔄 Initialize
    // -------------------------------------------------------
    init {
        refreshAll()
    }

    // -------------------------------------------------------
    // 🔄 Refresh All Dashboard Data
    // -------------------------------------------------------
    fun refreshAll() {
        refreshDate()
        refreshHabitSummary()
        refreshHydrationSummary()
        refreshLatestMood()
        refreshStreak()
    }

    // -------------------------------------------------------
    // 🗓️ Current Date
    // -------------------------------------------------------
    private fun refreshDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        _currentDate.value = dateFormat.format(Date())
    }

    // -------------------------------------------------------
    // 💪 Habit Summary
    // -------------------------------------------------------
    private fun refreshHabitSummary() {
        val habitsDone = dataManager.getHabitsDoneToday()
        val totalHabits = dataManager.getTotalHabits()
        val percent = if (totalHabits > 0) (habitsDone * 100) / totalHabits else 0

        val summaryText = "$habitsDone of $totalHabits Habits Completed"
        val quickSummaryText = "$habitsDone/$totalHabits"

        _habitSummary.value = HabitSummary(percent, summaryText, quickSummaryText)
    }

    // -------------------------------------------------------
    // 💧 Hydration Summary
    // -------------------------------------------------------
    private fun refreshHydrationSummary() {
        val waterIntake = dataManager.getWaterToday()
        val waterGoal = dataManager.getDailyWaterGoal()  // ✅ fixed name
        val percent = if (waterGoal > 0) (waterIntake * 100) / waterGoal else 0

        val intakeText = "$waterIntake ml of $waterGoal ml"
        val quickIntakeText = "$waterIntake ml"

        _hydrationSummary.value = HydrationSummary(percent, intakeText, quickIntakeText)
    }

    // -------------------------------------------------------
    // 😄 Latest Mood
    // -------------------------------------------------------
    private fun refreshLatestMood() {
        val latestMoodData = dataManager.getLatestMood()
        _latestMood.value = latestMoodData
    }

    // -------------------------------------------------------
    // 🔥 Current Streak
    // -------------------------------------------------------
    private fun refreshStreak() {
        val streakCount = dataManager.getCurrentStreak()
        _streak.value = "$streakCount Day${if (streakCount != 1) "s" else ""}"
    }
}

// -------------------------------------------------------
// 📘 Support Data Classes
// -------------------------------------------------------
data class HabitSummary(
    val percent: Int = 0,
    val summaryText: String = "",
    val quickSummaryText: String = ""
)

data class HydrationSummary(
    val percent: Int = 0,
    val intakeText: String = "",
    val quickIntakeText: String = ""
)

data class MoodData(
    val emoji: String = "😊",
    val label: String = "Happy"
)
