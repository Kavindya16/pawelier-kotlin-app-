package com.example.pawelierapp

import java.net.IDN

data class Product(val id: Int, val title: String, val price: String, val imageRes: Int)
data class CartLine(
    val product: Product,
    var qty: Int =1
)
{

}