package com.example.hbbittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hbbittracker.databinding.ActivityHomeBinding
import com.example.hbbittracker.ui.hydration.HydrationFragment
import com.example.hbbittracker.ui.mood.MoodFragment
import com.example.hbbittracker.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Safe View Lookup Function
        fun <T : View> safeFind(id: Int): T? = try {
            binding.root.findViewById(id)
        } catch (_: Exception) {
            null
        }

        // ✅ Initialize cards safely (no type cast crash)
        val cardHabits = safeFind<View>(R.id.cardHabits)
        val cardMood = safeFind<View>(R.id.cardMood)
        val cardHydration = safeFind<View>(R.id.cardHydration)

        val cardWater = safeFind<View>(R.id.cardWater)

        // ✅ Card click events
        cardHabits?.setOnClickListener {
            startActivity(Intent(this, DailyHabitsActivity::class.java))
        }

        cardMood?.setOnClickListener {
            openFragment(MoodFragment())
        }

        cardHydration?.setOnClickListener {
            openFragment(HydrationFragment())
        }

        cardWater?.setOnClickListener {
            openFragment(HydrationFragment())
        }



        // ✅ Bottom Navigation linking
        val bottomNav: BottomNavigationView = binding.bottomNavigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Already on Home", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_habits -> {
                    startActivity(Intent(this, DailyHabitsActivity::class.java))
                    true
                }
                R.id.nav_mood -> {
                    openFragment(MoodFragment())
                    true
                }
                R.id.nav_hydration -> {
                    openFragment(HydrationFragment())
                    true
                }
                R.id.nav_profile -> {
                    openFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    // ✅ Fragment loader (universal, safe)
    private fun openFragment(fragment: Fragment) {
        try {
            val containerId = resources.getIdentifier(
                "fragment_container_view",
                "id",
                packageName
            )

            val transaction = supportFragmentManager.beginTransaction()
                .replace(if (containerId != 0) containerId else android.R.id.content, fragment)
                .addToBackStack(null)

            transaction.commitAllowingStateLoss()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to open fragment", Toast.LENGTH_SHORT).show()
        }
    }
}
