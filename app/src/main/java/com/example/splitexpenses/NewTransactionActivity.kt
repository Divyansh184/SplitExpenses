package com.example.splitexpenses
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
            // Start activity to select a user
            val intent = Intent(this, SelectUserActivity::class.java)
            startActivityForResult(intent, 100)  // 100 is the request code for user selection
        }

        saveButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()

            if (amount != null && selectedUserEmail != null) {
                saveTransaction(amount)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedUserEmail = data?.getStringExtra("selectedUserEmail")
            addUserButton.text = selectedUserEmail  // Update button with selected user's email
        }
    }

    private fun saveTransaction(amount: Double) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val currentUserId = currentUser.uid

            // Save split transaction
            val transaction = mapOf(
                "amount" to amount,
                "lentBy" to currentUser.email,
                "borrowedBy" to selectedUserEmail
            )

            db.collection("users").document(currentUserId).collection("splits")
                .add(transaction)
                .addOnSuccessListener {
                    finish() // Go back to the main split screen
                }
        }
    }
}
