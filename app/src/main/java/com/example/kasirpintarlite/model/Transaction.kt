package com.example.kasirpintarlite.model

data class Transaction(
    var transactionId: String? = null,
    var userId: String? = null,
    var totalPrice: Int? = null,
    var date: String? = null
)
