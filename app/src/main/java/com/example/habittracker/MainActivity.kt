package com.example.habittracker

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    // Fragment instances
    private val homeFragment = HomeFragment()
    private val hydrationFragment = HydrationFragment()
    private val moodJournalFragment = MoodJournalFragment()
    private val settingsFragment = SettingsFragment()

    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Hide action bar
        supportActionBar?.hide()

        setupBottomNavigation()
        setupFragments()
        bottomNavigationView.selectedItemId = R.id.home
        requestNotificationPermissionIfNeeded()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_nav)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    switchFragment(homeFragment)
                    true
                }
                R.id.hydration -> {
                    switchFragment(hydrationFragment)
                    true
                }
                R.id.mood_journal -> {
                    switchFragment(moodJournalFragment)
                    true
                }
                R.id.settings -> {
                    switchFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFragments() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, homeFragment, "HOME")
            add(R.id.fragmentContainer, hydrationFragment, "HYDRATION")
            add(R.id.fragmentContainer, moodJournalFragment, "MOOD")
            add(R.id.fragmentContainer, settingsFragment, "SETTINGS")

            hide(hydrationFragment)
            hide(moodJournalFragment)
            hide(settingsFragment)

            commit()
        }

        activeFragment = homeFragment
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Optional: show rationale/toast. Keeping silent to avoid UI spam.
            }
        }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val permissionState = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun switchFragment(targetFragment: Fragment) {
        if (targetFragment == activeFragment) return

        supportFragmentManager.beginTransaction().apply {
            hide(activeFragment)
            show(targetFragment)
            commit()
        }
        activeFragment = targetFragment
    }
}