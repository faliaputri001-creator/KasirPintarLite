package com.example.kasirpintarlite.ui.transaction

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.model.Product

/**
 * Adapter untuk menampilkan daftar produk di layar Transaksi.
 * Menggunakan BaseAdapter untuk ListView.
 */
class TransactionAdapter(
    private val ctx: Context,
    private val items: List<Product>,
    private val onQtyChanged: () -> Unit
) : BaseAdapter() {

    // Menyimpan jumlah (qty) terpilih berdasarkan ID Produk
    // Menggunakan String (non-nullable) sebagai Key
    val qtyMap = mutableMapOf<String, Int>()

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        // 1. Inisialisasi View & ViewHolder (Pattern untuk ListView agar ringan)
        if (convertView == null) {
            view = LayoutInflater.from(ctx)
                .inflate(R.layout.item_transaction_product, parent, false)

            holder = ViewHolder(
                name = view.findViewById(R.id.txtName),
                price = view.findViewById(R.id.txtPrice),
                qty = view.findViewById(R.id.txtQty),
                // Pastikan di XML ini adalah ImageButton
                btnPlus = view.findViewById(R.id.btnPlus),
                btnMinus = view.findViewById(R.id.btnMinus)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        // 2. Ambil data produk berdasarkan posisi
        val product = items[position]
        val productId = product.id ?: "" // Konversi String? ke String agar aman

        // 3. Tampilkan data ke UI
        val currentQty = qtyMap[productId] ?: 0
        holder.name.text = product.name
        holder.price.text = "Rp ${product.price}"
        holder.qty.text = currentQty.toString()

        // 4. Logika Tombol Tambah (+)
        holder.btnPlus.setOnClickListener {
            val newQty = (qtyMap[productId] ?: 0) + 1
            qtyMap[productId] = newQty

            // Update tampilan lokal tanpa merefresh seluruh list (biar cepat)
            holder.qty.text = newQty.toString()
            onQtyChanged()
        }

        // 5. Logika Tombol Kurang (-)
        holder.btnMinus.setOnClickListener {
            val oldQty = qtyMap[productId] ?: 0
            if (oldQty > 0) {
                val newQty = oldQty - 1

                if (newQty == 0) {
                    qtyMap.remove(productId)
                } else {
                    qtyMap[productId] = newQty
                }

                holder.qty.text = newQty.toString()
                onQtyChanged()
            }
        }

        return view
    }

    /**
     * Objek penampung view agar tidak melakukan findViewById berulang-ulang
     */
    private data class ViewHolder(
        val name: TextView,
        val price: TextView,
        val qty: TextView,
        val btnPlus: ImageButton,  // Harus sinkron dengan XML
        val btnMinus: ImageButton  // Harus sinkron dengan XML
    )
}
