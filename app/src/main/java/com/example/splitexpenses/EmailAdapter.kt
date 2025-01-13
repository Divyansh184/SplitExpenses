package com.example.splitexpenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmailAdapter(private val emails: List<String>) :
    RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_email, parent, false)
        return EmailViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(emails[position])
    }

    override fun getItemCount(): Int {
        return emails.size
    }

    class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)

        fun bind(email: String) {
            emailTextView.text = email
        }
    }
}
