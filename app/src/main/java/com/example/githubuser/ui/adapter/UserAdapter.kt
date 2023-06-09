package com.example.githubuser.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubuser.R

class UserAdapter(private val userList: List<String>, private val imgAvatar: List<String>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvUsername.text = userList[position]
        Glide.with(holder.itemView.context)
            .load(imgAvatar[position])
            .into(holder.imgAvatar)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(
                userList[holder.adapterPosition],
                imgAvatar[holder.absoluteAdapterPosition]
            )
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(username: String, url: String)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        val imgAvatar: ImageView = view.findViewById(R.id.img_user_avatar)
    }
}
