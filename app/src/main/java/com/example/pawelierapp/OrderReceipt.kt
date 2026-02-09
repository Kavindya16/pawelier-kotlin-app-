package com.example.pawelierapp

data class OrderReceipt(
    val orderId: String,
    val total: Double,
    val message: String,
    val etaDays: Int = 3
)