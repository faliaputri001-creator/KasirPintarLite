package com.example.kasirpintarlite.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.auth.LoginActivity
import com.example.kasirpintarlite.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Set Layout (Pastikan activity_splash.xml tidak bermasalah)
        setContentView(R.layout.activity_splash)

        // 2. Gunakan Handler yang aman untuk berpindah layar
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 2000)
    }

    private fun checkUserStatus() {
        if (isFinishing) return

        try {
            // Firebase biasanya sudah inisialisasi otomatis via google-services plugin.
            // Kita langsung panggil FirebaseAuth.
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser

            if (user != null) {
                // 🔑 SUDAH LOGIN
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // ❌ BELUM LOGIN
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        } catch (e: Exception) {
            // Jika terjadi error Firebase (misal: google-services.json tidak valid)
            Log.e("SplashActivity", "Firebase Error: ${e.message}")

            // Jangan biarkan layar hitam, lempar ke Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
