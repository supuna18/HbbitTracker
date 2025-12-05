package com.example.hbbittracker.ui.habits

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hbbittracker.R
import com.example.hbbittracker.adapters.HabitAdapter
import com.example.hbbittracker.models.Habit
import com.example.hbbittracker.viewmodel.DashboardViewModel
import org.json.JSONArray
import org.json.JSONObject

class HabitsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HabitAdapter
    private val habitList = mutableListOf<Habit>()
    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habits, container, false)
        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]

        recyclerView = view.findViewById(R.id.recyclerHabits)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadHabits()

        adapter = HabitAdapter(
            habitList,
            onHabitUpdated = {
                saveHabits()
                Toast.makeText(requireContext(), "Habit updated", Toast.LENGTH_SHORT).show()
                viewModel.refreshAll()
            },
            onHabitDeleted = { position ->
                deleteHabit(position)
                viewModel.refreshAll()
            }
        )
        recyclerView.adapter = adapter

        val fabAddHabit: View = view.findViewById(R.id.fabAddHabit)
        fabAddHabit.setOnClickListener { showAddHabitDialog() }

        return view
    }

    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val editName = dialogView.findViewById<EditText>(R.id.editHabitName)
        val editGoal = dialogView.findViewById<EditText>(R.id.editHabitGoal)

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = editName.text.toString().trim()
                val goal = editGoal.text.toString().toIntOrNull() ?: 1

                if (name.isNotEmpty()) {
                    val newHabit = Habit(name, goal, 0)
                    habitList.add(newHabit)
                    adapter.notifyItemInserted(habitList.size - 1)
                    saveHabits()
                    Toast.makeText(requireContext(), "Habit added!", Toast.LENGTH_SHORT).show()
                    viewModel.refreshAll()
                } else {
                    Toast.makeText(requireContext(), "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteHabit(position: Int) {
        habitList.removeAt(position)
        adapter.notifyItemRemoved(position)
        saveHabits()
        Toast.makeText(requireContext(), "Habit deleted", Toast.LENGTH_SHORT).show()
    }

    private fun saveHabits() {
        val jsonArray = JSONArray()
        for (habit in habitList) {
            val obj = JSONObject()
            obj.put("name", habit.name)
            obj.put("goal", habit.goal)
            obj.put("progress", habit.progress)
            jsonArray.put(obj)
        }
        val prefs = requireContext().getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("habits", jsonArray.toString()).apply()
    }

    private fun loadHabits() {
        val prefs = requireContext().getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)
        val data = prefs.getString("habits", null)
        habitList.clear()

        if (data != null) {
            val jsonArray = JSONArray(data)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                habitList.add(
                    Habit(
                        obj.getString("name"),
                        obj.getInt("goal"),
                        obj.getInt("progress")
                    )
                )
            }
        }
    }
}
