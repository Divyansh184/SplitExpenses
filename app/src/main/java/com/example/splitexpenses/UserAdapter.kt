package com.example.splitexpenses

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// UserAdapter
class UserAdapter(
    private val userList: List<String>,
    private val onUserSelected: (String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userEmailText: TextView = itemView.findViewById(R.id.userEmailText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val email = userList[position]
        holder.userEmailText.text = email
        holder.itemView.setOnClickListener {
            onUserSelected(email)  // Handle user selection
        }
    }

        override fun getItemCount(): Int = userList.size
}
class SplitAdapter(private val splits: List<Split>) : RecyclerView.Adapter<SplitAdapter.SplitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SplitViewHolder {
        // Inflate the layout for each split item in RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_split, parent, false)
        return SplitViewHolder(view)
    }

    override fun onBindViewHolder(holder: SplitViewHolder, position: Int) {
        val split = splits[position]
        holder.bind(split)
    }

    override fun getItemCount(): Int = splits.size

    class SplitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val splitNameTextView: TextView = itemView.findViewById(R.id.splitNameTextView)
        private val splitAmountTextView: TextView = itemView.findViewById(R.id.splitAmountTextView)

        fun bind(split: Split) {
            splitNameTextView.text = split.borrowedBy   // Display borrowedBy instead of name
            splitAmountTextView.text = split.amount.toString()
        }

    }
}