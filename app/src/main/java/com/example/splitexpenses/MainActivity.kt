package com.example.splitexpenses

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private lateinit var totalExpenseText: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Launcher to handle results from AddEditExpenseActivity
    private val addExpenseLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // When the result is OK, reload the expenses
            loadExpenses()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        totalExpenseText = findViewById(R.id.totalExpenseText)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load the expenses initially
        loadExpenses()

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddEditExpenseActivity::class.java)
            addExpenseLauncher.launch(intent) // Use launcher to handle the result
        }
    }

    // Inflate the menu to show the options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)  // Inflate the menu
        return true
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                // Log out the user
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

                // Redirect to the login activity
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish() // Close the MainActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadExpenses() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val expensesRef = db.collection("users").document(userId).collection("expenses")

            expensesRef.get()
                .addOnSuccessListener { result ->
                    val expenses = result.map { document ->
                        val expense = document.toObject(Expense::class.java)
                        expense.copy(id = document.id)
                    }.toMutableList()

                    expenses.sortByDescending { it.timestamp }

                    val totalExpense = expenses.sumOf { it.amount } // Sum all expenses
                    totalExpenseText.text = "Total Expense: ₹${"%.2f".format(totalExpense)}" // Update total

                    adapter = ExpenseAdapter(expenses, ::editExpense, ::deleteExpense)
                    recyclerView.adapter = adapter
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load expenses: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun editExpense(expense: Expense) {
        val intent = Intent(this, AddEditExpenseActivity::class.java)
        intent.putExtra("expenseId", expense.id)
        intent.putExtra("amount", expense.amount)
        intent.putExtra("description", expense.description)
        addExpenseLauncher.launch(intent) // Launch for result to update RecyclerView
    }

    private fun deleteExpense(expense: Expense) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val expensesRef = db.collection("users").document(userId).collection("expenses")

            expensesRef.document(expense.id).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show()
                    loadExpenses()  // Reload expenses to update the RecyclerView
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to delete expense: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
