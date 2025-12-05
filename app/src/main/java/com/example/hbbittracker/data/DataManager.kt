package com.example.hbbittracker.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.hbbittracker.models.MoodEntry
import com.example.hbbittracker.viewmodel.MoodData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

/**
 * Context optional (default null). If context is passed we use SharedPreferences.
 * If not, we keep data in-memory (so DashboardViewModel() can still compile).
 */
class DataManager(context: Context? = null) {

    // ----- storage plumbing -----
    private val appContext = context?.applicationContext
    private val prefs: SharedPreferences? =
        appContext?.getSharedPreferences("HabitTrackerPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // in-memory fallbacks (when prefs == null)
    private val memWater = mutableMapOf<String, Int>()
    private val memMoods = mutableListOf<MoodEntry>()

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // ---------------- Hydration ----------------
    fun addWater(amount: Int) {
        val k = todayKey()
        val newVal = getWaterToday() + amount
        if (prefs != null) prefs.edit { putInt("water_$k", newVal) }
        else memWater[k] = newVal
    }

    fun resetWater() {
        val k = todayKey()
        if (prefs != null) prefs.edit { putInt("water_$k", 0) }
        else memWater[k] = 0
    }

    fun getWaterToday(): Int {
        val k = todayKey()
        return if (prefs != null) prefs.getInt("water_$k", 0) else memWater[k] ?: 0
    }

    fun getDailyWaterGoal(): Int =
        prefs?.getInt("water_goal", 3000) ?: 3000

    fun getHydrationProgressPercent(): Int {
        val goal = getDailyWaterGoal()
        val cur = getWaterToday()
        return if (goal <= 0) 0 else ((cur * 100) / goal).coerceAtMost(100)
    }

    fun getWaterQuickText(): String = "${getWaterToday()} ml of ${getDailyWaterGoal()} ml"

    // ---------------- Mood ----------------
    private fun readMoodsFromPrefs(): MutableList<MoodEntry> {
        val json = prefs?.getString("moods", null) ?: return memMoods
        val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun writeMoodsToPrefs(list: List<MoodEntry>) {
        prefs?.edit { putString("moods", gson.toJson(list)) } ?: run {
            memMoods.clear(); memMoods.addAll(list)
        }
    }

    fun getAllMoods(): List<MoodEntry> =
        prefs?.let { readMoodsFromPrefs() } ?: memMoods

    fun saveMood(mood: MoodEntry) {
        val list = getAllMoods().toMutableList()
        list.add(0, mood)
        writeMoodsToPrefs(list)
    }

    fun editMood(time: Long, newEmoji: String, newLabel: String, newNote: String) {
        val list = getAllMoods().map {
            if (it.time == time) it.copy(emoji = newEmoji, label = newLabel, note = newNote) else it
        }
        writeMoodsToPrefs(list)
    }

    fun deleteMood(time: Long) {
        val list = getAllMoods().filterNot { it.time == time }
        writeMoodsToPrefs(list)
    }

    // Used by DashboardViewModel
    fun getLatestMood(): MoodData {
        val m = getAllMoods().firstOrNull()
        return if (m == null) MoodData("🙂", "Calm") else MoodData(m.emoji, m.label)
    }

    // ---------------- Habits / Streak (placeholders) ----------------
    fun getHabitsDoneToday(): Int = 3
    fun getTotalHabits(): Int = 5
    fun getCurrentStreak(): Int = 7
}
