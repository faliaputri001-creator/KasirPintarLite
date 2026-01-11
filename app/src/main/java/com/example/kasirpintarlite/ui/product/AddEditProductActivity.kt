package com.example.kasirpintarlite.ui.product

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kasirpintarlite.R
import com.example.kasirpintarlite.databinding.ActivityAddEditProductBinding
import com.example.kasirpintarlite.model.Product
import com.example.kasirpintarlite.notification.NotificationHelper
import com.google.firebase.database.FirebaseDatabase

class AddEditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditProductBinding
    private var product: Product? = null
    private val dbRef = FirebaseDatabase.getInstance().getReference("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupListeners()
    }

    /**
     * Mengambil data dari Intent jika dalam mode EDIT (bukan tambah baru)
     */
    private fun setupData() {
        product = intent.getSerializableExtra("PRODUCT") as? Product

        product?.let {
            binding.etName.setText(it.name)
            binding.etPrice.setText(it.price.toString())
            binding.etStock.setText(it.stock.toString())
            binding.btnSave.text = "PERBARUI PRODUK" // Ubah teks tombol jika mode edit
        }
    }

    /**
     * Menyiapkan klik listener untuk tombol simpan
     */
    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            // Beri animasi klik (UX)
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click))
            saveProduct()
        }
    }

    /**
     * Logika untuk validasi dan penyimpanan data ke Firebase
     */
    private fun saveProduct() {
        val name = binding.etName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString()
        val stockStr = binding.etStock.text.toString()

        // 1. Validasi Input
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Konversi Data dengan Aman
        val price = priceStr.toLongOrNull() ?: 0L
        val stock = stockStr.toIntOrNull() ?: 0

        // 3. Tentukan ID (Lama atau Baru)
        val id = product?.id ?: dbRef.push().key ?: return
        val updatedProduct = Product(id, name, price, stock)

        // 4. Simpan ke Firebase
        dbRef.child(id).setValue(updatedProduct).addOnSuccessListener {
            showSuccessStatus(name, isUpdate = product != null)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Menampilkan notifikasi dan Toast keberhasilan
     */
    private fun showSuccessStatus(name: String, isUpdate: Boolean) {
        val title = if (isUpdate) "Produk Diperbarui" else "Produk Ditambahkan"
        val message = "$name berhasil ${if (isUpdate) "diperbarui" else "ditambahkan"}"

        NotificationHelper.showNotification(this, title, message)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
