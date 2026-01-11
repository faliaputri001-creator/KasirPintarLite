package com.example.kasirpintarlite.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.ui.main.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "kasir_pintar_lite_notif"
    private const val CHANNEL_NAME = "Notifikasi Kasir"

    fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Setup Channel (Penting untuk Android 8.0 ke atas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi transaksi dan stok produk"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Action saat notifikasi diklik (Membuka aplikasi)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 3. Build Notifikasi (Persis seperti di gambar contoh)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo) // Ikon aplikasi kecil yang muncul di bar atas
            .setContentTitle(title)           // Judul tebal (seperti "To-Do List")
            .setContentText(message)          // Isi pesan (seperti "Kamu punya 1 tugas...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)  // Aksi klik
            .setAutoCancel(true)              // Hilang setelah diklik
            .setWhen(System.currentTimeMillis()) // Menampilkan waktu "sekarang"
            .setShowWhen(true)

        // 4. Munculkan Notifikasi
        // Menggunakan ID unik agar notifikasi tidak saling menumpuk
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
