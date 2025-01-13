package com.example.splitexpenses

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SelectUserActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadUsers()
    }

    private fun loadUsers() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Assuming you have stored user information in a Firestore "users" collection
            val usersRef = db.collection("users")

            usersRef.get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val userList = result.map { document ->
                            document.getString("email") ?: ""
                        }.filter { it.isNotEmpty() } // Filter out empty strings

                        adapter = UserAdapter(userList) { selectedUserEmail ->
                            val resultIntent = Intent().apply {
                                putExtra("selectedUserEmail", selectedUserEmail)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()  // Close activity and return selected user
                        }
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this, "No users found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load users: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
