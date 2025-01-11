package com.example.splitexpenses
data class Expense(
    val amount: Double = 0.0,
    val description: String = "",
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis() // This will store the timestamp
)
