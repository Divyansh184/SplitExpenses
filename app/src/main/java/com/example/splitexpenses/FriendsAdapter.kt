package com.example.splitexpenses

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class FriendAdapter(private val friendList: List<Friend>) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendList[position]
        holder.bind(friend)
    }

    override fun getItemCount(): Int = friendList.size

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val friendEmail = itemView.findViewById<TextView>(R.id.textViewFriendEmail)
        private val friendAmount = itemView.findViewById<TextView>(R.id.textViewFriendAmount)

        fun bind(friend: Friend) {
            friendEmail.text = friend.email
            friendAmount.text = friend.amount.toString()
        }
    }
}
