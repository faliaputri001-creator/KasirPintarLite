package com.example.kasirpintarlite.ui.stock

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.model.Product // <--- PENTING: Import ini
import com.google.firebase.database.FirebaseDatabase

class StockAdapter(val mContext: Context, val productList: List<Product>) :
    ArrayAdapter<Product>(mContext, R.layout.item_stock, productList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 1. Gunakan LayoutInflater yang benar
        val view = convertView ?: LayoutInflater.from(mContext).inflate(R.layout.item_stock, parent, false)

        val product = productList[position]

        // 2. Pastikan pemanggilan ID benar-benar merujuk ke 'view' hasil inflate
        val txtName = view.findViewById<TextView>(R.id.txtProductName)
        val txtStock = view.findViewById<TextView>(R.id.txtCurrentStock)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdateStock)

        // 3. Set data
        txtName?.text = product.name
        txtStock?.text = "Stok saat ini: ${product.stock}"

        btnUpdate?.setOnClickListener {
            showUpdateDialog(product)
        }

        return view
    }

    private fun showUpdateDialog(product: Product) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Update Stok: ${product.name}")

        val input = EditText(mContext)
        input.hint = "Masukkan jumlah stok baru"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setPositiveButton("Simpan") { _, _ ->
            val newStock = input.text.toString()
            if (newStock.isNotEmpty()) {
                updateFirebaseStock(product.id!!, newStock.toInt())
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun updateFirebaseStock(productId: String, newStock: Int) {
        val dbRef = FirebaseDatabase.getInstance().getReference("products").child(productId)
        dbRef.child("stock").setValue(newStock).addOnSuccessListener {
            Toast.makeText(mContext, "Stok berhasil diperbarui", Toast.LENGTH_SHORT).show()
        }
    }
}
