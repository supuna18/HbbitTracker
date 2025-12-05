package com.example.hbbittracker.models

data class Habit(
    var name: String,
    var goal: Int,
    var progress: Int = 0
)
