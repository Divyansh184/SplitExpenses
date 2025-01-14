package com.example.splitexpenses
data class Split(
    val id: String = "",
    val lentBy: String = "",     // Who lent the money (current user)
    val borrowedBy: String = "", // Who borrowed the money (selected friend)
    val amount: Double = 0.0,    // The amount of money
    val isLend: Boolean = true   // True if the current user lent, False if borrowed
)
