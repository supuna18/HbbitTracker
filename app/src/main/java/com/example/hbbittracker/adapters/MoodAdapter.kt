package com.example.hbbittracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hbbittracker.databinding.ItemMoodBinding
import com.example.hbbittracker.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(
    private val onEditClicked: (MoodEntry) -> Unit,
    private val onDeleteClicked: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.VH>() {

    private val items = mutableListOf<MoodEntry>()

    inner class VH(val b: ItemMoodBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val mood = items[position]
        holder.b.tvMoodEmoji.text = mood.emoji
        holder.b.tvMoodLabel.text = mood.label
        holder.b.tvMoodDate.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(mood.time))
        holder.b.tvMoodNote.text = if (mood.note.isBlank()) "—" else mood.note

        holder.b.btnEditMood.setOnClickListener { onEditClicked(mood) }
        holder.b.btnDeleteMood.setOnClickListener { onDeleteClicked(mood) }
    }

    // ----- helpers -----
    fun submitList(newList: List<MoodEntry>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeMood(mood: MoodEntry) {
        val index = items.indexOf(mood)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateMood(updatedMood: MoodEntry) {
        val index = items.indexOfFirst { it.time == updatedMood.time }
        if (index != -1) {
            items[index] = updatedMood
            notifyItemChanged(index)
        }
    }
}
