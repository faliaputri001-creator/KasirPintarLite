package com.example.kasirpintarlite.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.ui.home.HomeFragment
import com.example.kasirpintarlite.ui.home.HomeNavigationListener
import com.example.kasirpintarlite.ui.product.ProductFragment
import com.example.kasirpintarlite.ui.profile.ProfileFragment
import com.example.kasirpintarlite.ui.transaction.TransactionFragment
import com.example.kasirpintarlite.ui.transaction.history.TransactionHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.FragmentTransaction
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), HomeNavigationListener {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            openFragment(HomeFragment(), false)
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> openFragment(HomeFragment())
                R.id.nav_product -> openFragment(ProductFragment())
                R.id.nav_transaction -> openFragment(TransactionFragment())
                R.id.nav_history -> openFragment(TransactionHistoryFragment())
                R.id.nav_profile -> openFragment(ProfileFragment())
                else -> false
            }
            true
        }
    }

    override fun navigateTo(menuId: Int) {
        bottomNav.selectedItemId = menuId
    }

    // Fungsi openFragment yang ditingkatkan dengan Animasi & Safety
    private fun openFragment(fragment: Fragment, animate: Boolean = true) {
        if (isFinishing) return

        val transaction = supportFragmentManager.beginTransaction()

        if (animate) {
            // Animasi Transisi Modern
            transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        transaction.replace(R.id.fragment_container, fragment)
        transaction.commitAllowingStateLoss()
    }
}

