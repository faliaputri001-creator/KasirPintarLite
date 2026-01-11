package com.example.kasirpintarlite.ui.transaction.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.model.TransactionHistory
import com.example.kasirpintarlite.ui.transaction.history.detail.TransactionDetailActivity
import com.example.kasirpintarlite.utils.CsvHelper
import com.example.kasirpintarlite.utils.PdfHelper
import com.google.firebase.database.*

class TransactionHistoryFragment : Fragment() {

    private var _view: View? = null
    private val v get() = _view!!

    private lateinit var listView: ListView
    private lateinit var adapter: TransactionHistoryAdapter
    private val historyList = mutableListOf<TransactionHistory>()
    private val ref = FirebaseDatabase.getInstance().getReference("transactions")
    private var historyListener: ValueEventListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _view = inflater.inflate(R.layout.fragment_transaction_history, container, false)

        // Gunakan ImageButton sesuai XML redesign
        val btnPdf = v.findViewById<ImageButton>(R.id.btnExportPdf)
        val btnCsv = v.findViewById<ImageButton>(R.id.btnExportExcel)

        btnPdf?.setOnClickListener { exportPdf() }
        btnCsv?.setOnClickListener { exportCsv() }

        listView = v.findViewById(R.id.listHistory)
        adapter = TransactionHistoryAdapter(requireContext(), historyList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, pos, _ ->
            val intent = Intent(requireContext(), TransactionDetailActivity::class.java)
            intent.putExtra("transactionId", historyList[pos].id)
            startActivity(intent)
        }

        loadHistory()
        return v
    }

    private fun loadHistory() {
        historyListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded || _view == null) return

                historyList.clear()
                for (it in snapshot.children) {
                    val id = it.key ?: ""
                    // Ambil sebagai Long secara eksplisit untuk mencegah ClassCastException
                    val date = it.child("date").value.toString().toLongOrNull() ?: 0L
                    val total = it.child("total").value.toString().toLongOrNull() ?: 0L

                    historyList.add(TransactionHistory(id, date, total))
                }
                historyList.sortByDescending { it.date }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(historyListener!!)
    }

    // Fungsi exportPdf & exportCsv sesuaikan dengan helper yang sudah kita buat sebelumnya
    private fun exportPdf() { /* ... */ }
    private fun exportCsv() { /* ... */ }

    override fun onDestroyView() {
        super.onDestroyView()
        historyListener?.let { ref.removeEventListener(it) }
        _view = null
    }
}
