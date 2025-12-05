package com.example.hbbittracker.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hbbittracker.adapters.MoodAdapter
import com.example.hbbittracker.data.DataManager
import com.example.hbbittracker.databinding.FragmentMoodBinding
import com.example.hbbittracker.models.MoodEntry
import com.example.hbbittracker.viewmodel.DashboardViewModel

class MoodFragment : Fragment() {

    private lateinit var b: FragmentMoodBinding
    private lateinit var dm: DataManager
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var viewModel: DashboardViewModel

    private var selectedEmoji = "😊"
    private var editingMood: MoodEntry? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = FragmentMoodBinding.inflate(inflater, container, false)
        dm = DataManager(requireContext()) // use prefs
        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEmojiSelectors()
        setupRecyclerView()
        loadMoods()

        b.btnSave.setOnClickListener {
            val note = b.editNote.text.toString()
            val label = when (selectedEmoji) {
                "😊" -> "Happy"
                "😢" -> "Sad"
                "😴" -> "Tired"
                "😡" -> "Angry"
                "🙂" -> "Calm"
                else -> "Mood"
            }

            if (editingMood != null) {
                dm.editMood(editingMood!!.time, selectedEmoji, label, note)
                editingMood = null
            } else {
                dm.saveMood(
                    MoodEntry(
                        emoji = selectedEmoji,
                        label = label,
                        note = note,
                        time = System.currentTimeMillis()
                    )
                )
            }

            b.editNote.setText("")
            loadMoods()
            b.root.postDelayed({ viewModel.refreshAll() }, 200)
        }
    }

    private fun loadMoods() {
        val moods = dm.getAllMoods()
        moodAdapter.submitList(moods)
    }

    private fun setupEmojiSelectors() {
        val emojis = listOf(b.emojiHappy, b.emojiSad, b.emojiTired, b.emojiAngry, b.emojiCalm)
        emojis.forEach { tv -> tv.setOnClickListener { selectEmoji(it as TextView) } }
        selectEmoji(b.emojiHappy) // default
    }

    private fun selectEmoji(textView: TextView) {
        listOf(b.emojiHappy, b.emojiSad, b.emojiTired, b.emojiAngry, b.emojiCalm)
            .forEach { it.setBackgroundResource(android.R.color.transparent) }

        // simple highlight (avoid missing material drawable errors)
        textView.setBackgroundColor(requireContext().getColor(android.R.color.holo_blue_light))
        selectedEmoji = textView.text.toString()
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            onEditClicked = { moodEntry ->
                editingMood = moodEntry
                b.editNote.setText(moodEntry.note)
                selectEmoji(
                    when (moodEntry.emoji) {
                        "😊" -> b.emojiHappy
                        "😢" -> b.emojiSad
                        "😴" -> b.emojiTired
                        "😡" -> b.emojiAngry
                        "🙂" -> b.emojiCalm
                        else -> b.emojiHappy
                    }
                )
            },
            onDeleteClicked = { moodEntry ->
                dm.deleteMood(moodEntry.time)
                loadMoods()
                viewModel.refreshAll()
            }
        )
        b.recyclerMood.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerMood.adapter = moodAdapter
    }
}
