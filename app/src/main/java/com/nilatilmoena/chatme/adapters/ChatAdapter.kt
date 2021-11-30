package com.nilatilmoena.chatme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nilatilmoena.chatme.models.ChatModel
import com.nilatilmoena.chatme.R
import java.util.ArrayList

class ChatAdapter(var messageArrayList: ArrayList<ChatModel?>, var token: String) :
    RecyclerView.Adapter<ChatAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chat_item_sender, parent, false)
            ListViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chat_item_receiver, parent, false)
            ListViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val message = messageArrayList[position]
        holder.showMessage.setText(message?.message)
        holder.showTime.setText(message?.time_stamp)
    }

    override fun getItemCount(): Int {
        return messageArrayList.size
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var showMessage: TextView
        var showTime: TextView

        init {
            showMessage = itemView.findViewById(R.id.show_message)
            showTime = itemView.findViewById(R.id.show_time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageArrayList[position]?.sender.equals(token)) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_lEFT
        }
    }

    companion object {
        const val MSG_TYPE_lEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }
}