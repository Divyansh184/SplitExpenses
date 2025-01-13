package com.example.splitexpenses

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplitWithFriendsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SplitAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_with_friends)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, NewTransactionActivity::class.java)
            startActivity(intent)
        }

        loadSplits()
    }

    private fun loadSplits() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val splitsRef = db.collection("users").document(userId).collection("splits")

            splitsRef.get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        Toast.makeText(this, "No splits found.", Toast.LENGTH_SHORT).show()
                    } else {
                        val splits = result.map { document ->
                            val split = document.toObject(Split::class.java)
                            Log.d("Split Data", "LentBy: ${split.lentBy}, BorrowedBy: ${split.borrowedBy}, Amount: ${split.amount}")
                            split.copy(id = document.id)
                        }.toMutableList()

                        adapter = SplitAdapter(splits)
                        recyclerView.adapter = adapter
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load splits: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }
}

