package com.example.hbbittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var nextBtn: Button
    private lateinit var skipBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Initialize views
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        nextBtn = findViewById(R.id.buttonNext)
        skipBtn = findViewById(R.id.buttonSkip)

        // Onboarding pages data
        val items = listOf(
            OnboardingItem(R.drawable.ic_habbit, "Track Habits", "Stay consistent with your daily goals."),
            OnboardingItem(R.drawable.ic_mood, "Mood Journal", "Log your feelings with emojis.")
        )

        val adapter = OnboardingAdapter(items)
        viewPager.adapter = adapter

        // Tab indicators
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // Next button
        nextBtn.setOnClickListener {
            if (viewPager.currentItem < items.size - 1) {
                viewPager.currentItem += 1
            } else {
                // Last page → move to Login
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        //Skip button
        skipBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
