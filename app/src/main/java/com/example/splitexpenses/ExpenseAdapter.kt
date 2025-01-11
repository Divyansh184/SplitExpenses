package com.example.splitexpenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val onEditClick: (Expense) -> Unit,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.amountTextView.text = "₹${expense.amount}"
        holder.descriptionTextView.text = expense.description

        // Format the timestamp to a readable date
        val date = Date(expense.timestamp)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
        holder.timestampTextView.text = formattedDate

        holder.editButton.setOnClickListener { onEditClick(expense) }
        holder.deleteButton.setOnClickListener { onDeleteClick(expense) }
    }

    override fun getItemCount(): Int = expenses.size

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}
