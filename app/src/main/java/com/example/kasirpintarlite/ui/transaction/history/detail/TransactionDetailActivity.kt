package com.example.kasirpintarlite.ui.transaction.history.detail

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.model.TransactionItem
import com.google.firebase.database.*

class TransactionDetailActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var listView: ListView
    private lateinit var txtTotal: TextView
    private val itemList = mutableListOf<TransactionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)

        // 1. Inisialisasi View (Pastikan ID sama dengan activity_transaction_detail.xml)
        listView = findViewById(R.id.listDetail)
        txtTotal = findViewById(R.id.txtTotalDetail)

        // 2. Ambil ID Transaksi dari Intent
        val transactionId = intent.getStringExtra("transactionId")
        if (transactionId == null) {
            Toast.makeText(this, "ID Transaksi tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 3. Setup Firebase Reference
        dbRef = FirebaseDatabase.getInstance().getReference("transactions").child(transactionId)

        // 4. Muat Data
        loadTransactionDetail()
    }

    private fun loadTransactionDetail() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return

                // PERBAIKAN: Ambil total sebagai Long agar tidak ClassCastException
                val total = snapshot.child("total").value.toString().toLongOrNull() ?: 0L
                txtTotal.text = "Rp ${formatRupiah(total)}"

                // Ambil daftar item (barang yang dibeli)
                itemList.clear()
                val itemsSnapshot = snapshot.child("items")
                for (data in itemsSnapshot.children) {
                    val item = data.getValue(TransactionItem::class.java)
                    item?.let { itemList.add(it) }
                }

                // Pasang Adapter
                setupAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TransactionDetailActivity, "Gagal memuat: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAdapter() {
        val adapter = TransactionDetailAdapter(this, itemList)
        listView.adapter = adapter
    }

    /**
     * Helper sederhana untuk format teks harga (opsional)
     */
    private fun formatRupiah(amount: Long): String {
        return "%,d".format(amount).replace(',', '.')
    }
}
