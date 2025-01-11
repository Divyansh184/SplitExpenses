package com.example.splitexpenses

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddEditExpenseActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var isEditing = false
    private var expenseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_expense)

        amountEditText = findViewById(R.id.amountEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Check if the activity was started for editing an existing expense
        val intent = intent
        if (intent.hasExtra("expenseId")) {
            isEditing = true
            expenseId = intent.getStringExtra("expenseId")
            val amount = intent.getDoubleExtra("amount", 0.0)
            val description = intent.getStringExtra("description")

            // Pre-fill the fields with existing data for editing
            amountEditText.setText(amount.toString())
            descriptionEditText.setText(description)
        }

        // Save button action
        saveButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val description = descriptionEditText.text.toString()

            if (amount == null || description.isEmpty()) {
                Toast.makeText(this, "Please enter valid data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditing) {
                updateExpense(expenseId!!, amount, description)
            } else {
                addNewExpense(amount, description)
            }
        }

        // Cancel button action
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    // Function to add a new expense
    private fun addNewExpense(amount: Double, description: String) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val expense = Expense(
                id = UUID.randomUUID().toString(),
                amount = amount,
                description = description,
                timestamp = System.currentTimeMillis()
            )

            db.collection("users").document(userId).collection("expenses")
                .add(expense)
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add expense: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to update an existing expense
    private fun updateExpense(expenseId: String, amount: Double, description: String) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val expenseRef = db.collection("users").document(userId).collection("expenses").document(expenseId)

            expenseRef.update(mapOf(
                "amount" to amount,
                "description" to description,
                "timestamp" to System.currentTimeMillis()
            ))
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update expense: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
