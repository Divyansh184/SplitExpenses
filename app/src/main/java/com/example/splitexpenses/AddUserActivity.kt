package com.example.splitexpenses

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AddUserActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        // Use the correct RecyclerView id from the XML
        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadAllUsers()
    }

    private fun loadAllUsers() {
        db.collection("users").get().addOnSuccessListener { result ->
            val userList = mutableListOf<String>()
            for (document in result) {
                val email = document.getString("email") ?: ""
                userList.add(email)
            }

            val userAdapter = UserAdapter(userList) { selectedUser ->
                // Open a dialog to ask for the amount
                showAmountDialog(selectedUser)
            }

            recyclerView.adapter = userAdapter
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to load users: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAmountDialog(selectedUser: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dailog_enter_amount, null)
        val amountEditText = dialogView.findViewById<EditText>(R.id.editTextAmount)

        AlertDialog.Builder(this)
            .setTitle("Enter Amount to Split")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val amount = amountEditText.text.toString().toDoubleOrNull()
                if (amount != null) {
                    // Save the expense and go back to the previous activity
                    val intent = Intent(this, AddExpenseWithFriendsActivity::class.java)
                    intent.putExtra("selectedUser", selectedUser)
                    intent.putExtra("amount", amount)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
