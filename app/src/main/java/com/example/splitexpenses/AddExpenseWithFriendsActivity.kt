package com.example.splitexpenses

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class AddExpenseWithFriendsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense_with_friends)

        val selectedUserEmail = intent.getStringExtra("selectedUser") ?: return

        val amountEditText = findViewById<EditText>(R.id.editTextAmount)
        val addExpenseButton = findViewById<Button>(R.id.buttonAddExpense)

        addExpenseButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDouble()
            addExpense(selectedUserEmail, amount)
        }
    }

    private fun addExpense(friendEmail: String, amount: Double) {
        currentUser?.let { user ->
            val userEmail = user.email ?: return
            val friendRef = db.collection("users").document(user.uid).collection("expenses")
                .document(friendEmail)

            val expense = hashMapOf(
                "email" to friendEmail,
                "amount" to amount
            )

            friendRef.set(expense).addOnSuccessListener {
                finish() // Close and return to SplitWithFriendsActivity
            }
        }
    }
}
