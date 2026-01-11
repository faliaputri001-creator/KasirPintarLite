package com.example.kasirpintarlite.model

data class Product(
    val id: String = "",       // Ubah dari String? ke String agar lebih mudah
    val name: String = "",     // Beri default value kosong
    val price: Long = 0L,      // Pastikan Long
    val stock: Int = 0,        // Pastikan Int
    val category: String = ""
)
