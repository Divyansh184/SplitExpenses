package com.example.splitexpenses

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewTransactionActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var addUserButton: Button
    private var selectedUserEmail: String? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_transaction)

        amountEditText = findViewById(R.id.amountEditText)
        saveButton = findViewById(R.id.saveButton)
        addUserButton = findViewById(R.id.addUserButton)

        addUserButton.setOnClickListener {
            val intent = Intent(this, SelectUserActivity::class.java)
            startActivityForResult(intent, 100)
        }

        saveButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()

            if (amount != null && selectedUserEmail != null) {
                saveTransaction(amount)
            } else {
                Toast.makeText(this, "Please enter a valid amount and select a user.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedUserEmail = data?.getStringExtra("selectedUserEmail")
            addUserButton.text = selectedUserEmail
        }
    }

    private fun saveTransaction(amount: Double) {
        val currentUser = auth.currentUser
        if (currentUser != null && selectedUserEmail != null) {
            val currentUserId = currentUser.uid

            // Split the amount equally
            val halfAmount = amount / 2

            // First half: Save the current user lending the money
            val lendTransaction = mapOf(
                "amount" to halfAmount,
                "lentBy" to currentUser.email,
                "borrowedBy" to selectedUserEmail,
                "isLend" to true
            )

            // Save the lending transaction for the current user
            db.collection("users").document(currentUserId).collection("splits")
                .add(lendTransaction)
                .addOnSuccessListener {
                    // Second half: Save the other user borrowing the money
                    db.collection("users").whereEqualTo("email", selectedUserEmail)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val otherUserId = querySnapshot.documents[0].id

                                val borrowTransaction = mapOf(
                                    "amount" to halfAmount,
                                    "lentBy" to currentUser.email,
                                    "borrowedBy" to selectedUserEmail,
                                    "isLend" to false
                                )

                                // Save the borrowing transaction for the other user
                                db.collection("users").document(otherUserId).collection("splits")
                                    .add(borrowTransaction)
                                    .addOnSuccessListener {
                                        finish() // Go back to the main split screen after success
                                    }
                            }
                        }
                }
        }
    }



    private fun addBorrowerTransaction(borrowerEmail: String, amount: Double, lenderEmail: String?) {
        // Find the user by email
        db.collection("users").whereEqualTo("email", borrowerEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
                } else {
                    val borrowerUserId = documents.documents[0].id

                    // Save the split transaction for the borrower
                    val borrowerTransaction = mapOf(
                        "amount" to amount,
                        "lentBy" to lenderEmail,
                        "borrowedBy" to borrowerEmail,
                        "type" to "borrow"
                    )

                    db.collection("users").document(borrowerUserId).collection("splits")
                        .add(borrowerTransaction)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Transaction split successfully!", Toast.LENGTH_SHORT).show()
                            finish() // Go back to the main split screen
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save borrower's transaction: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to find user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
