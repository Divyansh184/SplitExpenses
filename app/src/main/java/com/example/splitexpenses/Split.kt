package com.example.splitexpenses
data class Split(
    val id: String = "",
    val lentBy: String = "",     // Field for who lent the money
    val borrowedBy: String = "", // Field for who borrowed the money
    val amount: Double = 0.0     // The amount of money
)
