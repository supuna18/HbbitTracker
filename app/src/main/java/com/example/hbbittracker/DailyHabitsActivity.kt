package com.example.hbbittracker

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hbbittracker.adapters.HabitAdapter
import com.example.hbbittracker.models.Habit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.json.JSONArray
import org.json.JSONObject

class DailyHabitsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HabitAdapter
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var textProgressSummary: TextView
    private val habits = mutableListOf<Habit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_habits)

        // ✅ Setup UI Components
        recyclerView = findViewById(R.id.recyclerHabits)
        progressIndicator = findViewById(R.id.progressCircular)
        textProgressSummary = findViewById(R.id.textProgressSummary)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Load saved habits
        loadHabits()

        // ✅ Setup adapter with callbacks
        adapter = HabitAdapter(
            habits,
            onHabitUpdated = {
                saveHabits()
                updateProgressSummary()
            },
            onHabitDeleted = { position ->
                deleteHabit(position)
                updateProgressSummary()
            }
        )
        recyclerView.adapter = adapter

        // ✅ Floating Action Button → Add new habit
        val btnAddHabit: FloatingActionButton = findViewById(R.id.fabAddHabit)
        btnAddHabit.setOnClickListener { showAddHabitDialog() }

        // ✅ Initial progress update
        updateProgressSummary()
    }

    // ✅ Show dialog to add a new habit
    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val editName = dialogView.findViewById<EditText>(R.id.editHabitName)
        val editGoal = dialogView.findViewById<EditText>(R.id.editHabitGoal)

        AlertDialog.Builder(this)
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = editName.text.toString().trim()
                val goal = editGoal.text.toString().toIntOrNull() ?: 1

                if (name.isNotEmpty()) {
                    val newHabit = Habit(name, goal, 0)
                    habits.add(newHabit)
                    adapter.notifyItemInserted(habits.size - 1)
                    saveHabits()
                    updateProgressSummary()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ✅ Delete a habit
    private fun deleteHabit(position: Int) {
        habits.removeAt(position)
        adapter.notifyItemRemoved(position)
        saveHabits()
    }

    // ✅ Save habits into SharedPreferences
    private fun saveHabits() {
        val jsonArray = JSONArray()
        for (habit in habits) {
            val obj = JSONObject()
            obj.put("name", habit.name)
            obj.put("goal", habit.goal)
            obj.put("progress", habit.progress)
            jsonArray.put(obj)
        }

        val prefs = getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("habits", jsonArray.toString()).apply()
    }

    // ✅ Load habits from SharedPreferences
    private fun loadHabits() {
        val prefs = getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)
        val data = prefs.getString("habits", null)
        habits.clear()

        if (data != null) {
            val jsonArray = JSONArray(data)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                habits.add(
                    Habit(
                        obj.getString("name"),
                        obj.getInt("goal"),
                        obj.getInt("progress")
                    )
                )
            }
        }
    }

    // ✅ Update circular progress & text dynamically
    private fun updateProgressSummary() {
        val total = habits.size
        val completed = habits.count { it.progress >= it.goal }
        val remaining = total - completed

        if (total > 0) {
            val percent = (completed.toFloat() / total.toFloat()) * 100
            progressIndicator.setProgress(percent.toInt(), true)
            textProgressSummary.text = "$completed Completed • $remaining Remaining"
        } else {
            progressIndicator.setProgress(0, true)
            textProgressSummary.text = "No habits yet"
        }
    }
}
