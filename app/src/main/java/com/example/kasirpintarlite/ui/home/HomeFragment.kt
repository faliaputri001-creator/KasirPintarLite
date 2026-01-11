package com.example.kasirpintarlite.ui.home

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.ui.product.ProductFragment
import com.example.kasirpintarlite.ui.transaction.TransactionFragment
import com.example.kasirpintarlite.ui.transaction.history.TransactionHistoryFragment
import com.example.kasirpintarlite.ui.chart.SalesChartFragment
import com.example.kasirpintarlite.ui.management.ManagementFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var txtTotalSales: TextView
    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        txtTotalSales = view.findViewById(R.id.txtTotalSales)
        dbRef = FirebaseDatabase.getInstance().getReference("transactions")

        loadTodaySales()
        loadProfileHeader(view) // Panggil sinkronisasi profil
        setupQuickMenu(view)

        return view
    }

    private fun setupQuickMenu(view: View) {
        view.findViewById<View>(R.id.cardTransaction)?.setOnClickListener { openFragment(TransactionFragment()) }
        view.findViewById<View>(R.id.cardHistory)?.setOnClickListener { openFragment(TransactionHistoryFragment()) }
        view.findViewById<View>(R.id.cardProduct)?.setOnClickListener { openFragment(ProductFragment()) }
        view.findViewById<View>(R.id.cardChart)?.setOnClickListener { openFragment(SalesChartFragment()) }
        view.findViewById<View>(R.id.btnOpenManagement)?.setOnClickListener { openFragment(ManagementFragment()) }
    }

    private fun openFragment(fragment: Fragment) {
        if (!isAdded) return
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Memuat Foto Profil (Mendukung Base64 & URL)
     */
    private fun loadProfileHeader(view: View) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.child("profileImageUrl").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded || view == null) return

                val dataImage = snapshot.value?.toString() ?: ""
                if (dataImage.isNotEmpty() && dataImage != "null") {
                    try {
                        val imgHeader = view.findViewById<ImageView>(R.id.imgProfileSmall)

                        if (dataImage.startsWith("http")) {
                            // Jika format URL lama
                            imgHeader?.load(dataImage) {
                                crossfade(true)
                                transformations(CircleCropTransformation())
                            }
                        } else {
                            // Jika format Base64 (Metode Baru Gratis)
                            val imageBytes = Base64.decode(dataImage, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            imgHeader?.load(decodedImage) {
                                crossfade(true)
                                transformations(CircleCropTransformation())
                            }
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadTodaySales() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded || view == null) return
                var totalToday: Long = 0L
                val todayStart = getTodayStartMillis()
                for (data in snapshot.children) {
                    val date = data.child("date").getValue(Long::class.java) ?: 0L
                    val total = data.child("total").getValue(Long::class.java) ?: 0L
                    if (date >= todayStart) { totalToday += total }
                }
                txtTotalSales.text = "Rp $totalToday"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getTodayStartMillis(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
