package com.example.hbbittracker.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.hbbittracker.R
import com.example.hbbittracker.models.Habit

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onHabitUpdated: () -> Unit,
    private val onHabitDeleted: (Int) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHabitName: TextView = itemView.findViewById(R.id.textHabitName)
        val textHabitGoal: TextView = itemView.findViewById(R.id.textHabitGoal)
        val textCurrentCount: TextView = itemView.findViewById(R.id.textCurrentCount)
        val textProgressCount: TextView = itemView.findViewById(R.id.textProgressCount)
        val progressHabit: ProgressBar = itemView.findViewById(R.id.progressHabit)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val checkboxComplete: CheckBox = itemView.findViewById(R.id.checkboxComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun getItemCount(): Int = habits.size

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        // Basic info
        holder.textHabitName.text = habit.name
        holder.textHabitGoal.text = "Goal: ${habit.goal}"
        holder.progressHabit.max = habit.goal
        holder.progressHabit.progress = habit.progress
        holder.textCurrentCount.text = habit.progress.toString()
        holder.textProgressCount.text = "${habit.progress} / ${habit.goal}"
        holder.checkboxComplete.isChecked = habit.progress >= habit.goal

        // ✅ Checkbox – mark as complete/incomplete
        holder.checkboxComplete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                habit.progress = habit.goal
            } else {
                habit.progress = 0
            }
            holder.progressHabit.progress = habit.progress
            holder.textCurrentCount.text = habit.progress.toString()
            holder.textProgressCount.text = "${habit.progress} / ${habit.goal}"
            onHabitUpdated()
        }

        // ➕ Increase button
        holder.btnIncrease.setOnClickListener {
            if (habit.progress < habit.goal) {
                habit.progress++
                holder.progressHabit.progress = habit.progress
                holder.textCurrentCount.text = habit.progress.toString()
                holder.textProgressCount.text = "${habit.progress} / ${habit.goal}"
                holder.checkboxComplete.isChecked = habit.progress >= habit.goal
                onHabitUpdated()
            }
        }

        // ➖ Decrease button
        holder.btnDecrease.setOnClickListener {
            if (habit.progress > 0) {
                habit.progress--
                holder.progressHabit.progress = habit.progress
                holder.textCurrentCount.text = habit.progress.toString()
                holder.textProgressCount.text = "${habit.progress} / ${habit.goal}"
                holder.checkboxComplete.isChecked = habit.progress >= habit.goal
                onHabitUpdated()
            }
        }

        // ✏️ Edit habit dialog
        holder.btnEdit.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_add_habit, null)
            val editName = dialogView.findViewById<EditText>(R.id.editHabitName)
            val editGoal = dialogView.findViewById<EditText>(R.id.editHabitGoal)

            editName.setText(habit.name)
            editGoal.setText(habit.goal.toString())

            AlertDialog.Builder(context)
                .setTitle("Edit Habit")
                .setView(dialogView)
                .setPositiveButton("Save") { _, _ ->
                    val newName = editName.text.toString().trim()
                    val newGoal = editGoal.text.toString().toIntOrNull() ?: habit.goal

                    if (newName.isNotEmpty()) {
                        habit.name = newName
                        habit.goal = newGoal
                        if (habit.progress > habit.goal) habit.progress = habit.goal
                        notifyItemChanged(position)
                        onHabitUpdated()
                    } else {
                        Toast.makeText(context, "Habit name cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // 🗑 Delete habit
        holder.btnDelete.setOnClickListener {
            onHabitDeleted(position)
        }
    }
}
