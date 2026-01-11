package com.example.kasirpintarlite.model

import java.io.Serializable

data class TransactionHistory(
    val id: String = "",
    val date: Long = 0L,
    val total: Long = 0L // Pastikan Long
) : Serializable
