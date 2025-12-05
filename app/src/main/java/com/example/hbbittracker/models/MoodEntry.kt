package com.example.hbbittracker.models

data class MoodEntry(
    val emoji: String,
    val label: String,   // 🆕 Added label field
    val note: String,
    val time: Long
)
