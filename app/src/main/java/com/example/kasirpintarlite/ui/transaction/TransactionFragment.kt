package com.example.kasirpintarlite.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.model.Product
import com.example.kasirpintarlite.notification.NotificationHelper // 1. TAMBAHKAN IMPORT INI
import com.google.firebase.database.*

class TransactionFragment : Fragment() {

    private var _view: View? = null
    private val viewSafe get() = _view!!

    private lateinit var listView: ListView
    private lateinit var txtTotal: TextView
    private lateinit var adapter: TransactionAdapter

    private val productList = mutableListOf<Product>()
    private var productListener: ValueEventListener? = null
    private val productRef by lazy {
        FirebaseDatabase.getInstance().getReference("products")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _view = inflater.inflate(R.layout.fragment_transaction, container, false)

        listView = viewSafe.findViewById(R.id.listProducts)
        txtTotal = viewSafe.findViewById(R.id.txtTotal)

        adapter = TransactionAdapter(requireContext(), productList) {
            txtTotal.text = "Total: Rp ${calculateTotal()}"
        }
        listView.adapter = adapter

        viewSafe.findViewById<Button>(R.id.btnSaveTransaction).setOnClickListener {
            if (!isAdded) return@setOnClickListener
            saveTransaction()
        }

        loadProducts()
        return viewSafe
    }

    private fun calculateTotal(): Long {
        var total = 0L
        for (product in productList) {
            val productId = product.id ?: ""
            val qty = adapter.qtyMap[productId] ?: 0
            total += product.price * qty
        }
        return total
    }

    private fun saveTransaction() {
        if (!isAdded) return

        val total = calculateTotal()
        if (total == 0L) {
            context?.let {
                Toast.makeText(it, "Pilih produk dulu", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val transactionRef = FirebaseDatabase.getInstance().getReference("transactions")
        val pRef = FirebaseDatabase.getInstance().getReference("products")

        val id = transactionRef.push().key ?: return
        val items = mutableMapOf<String, Any>()

        for (product in productList) {
            val productId = product.id ?: ""
            val qty = adapter.qtyMap[productId] ?: 0

            if (qty > 0) {
                items[productId] = mapOf(
                    "name" to product.name,
                    "price" to product.price,
                    "qty" to qty,
                    "subtotal" to product.price * qty
                )

                pRef.child(productId)
                    .child("stock")
                    .setValue(product.stock - qty)
            }
        }

        transactionRef.child(id).setValue(
            mapOf(
                "date" to System.currentTimeMillis(),
                "total" to total,
                "items" to items
            )
        ).addOnSuccessListener {
            // 2. LETAK NOTIFIKASI DISINI (Setelah sukses simpan ke Firebase)
            context?.let { ctx ->
                // Munculkan Notifikasi Sistem
                NotificationHelper.showNotification(
                    ctx,
                    "Transaksi Berhasil",
                    "Penjualan sebesar Rp $total telah berhasil disimpan."
                )

                Toast.makeText(ctx, "Transaksi berhasil", Toast.LENGTH_SHORT).show()
            }

            // Reset tampilan
            adapter.qtyMap.clear()
            adapter.notifyDataSetChanged()
            txtTotal.text = "Total: Rp 0"
        }
    }

    private fun loadProducts() {
        productListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded || _view == null) return

                productList.clear()
                for (data in snapshot.children) {
                    val p = data.getValue(Product::class.java)
                    p?.let { productList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        productRef.addValueEventListener(productListener!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        productListener?.let { productRef.removeEventListener(it) }
        _view = null
    }
}
