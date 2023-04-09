package com.example.githubuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubuser.databinding.UserListBinding

class UserAdapter(private val userList: List<String>, private val imgAvatar: List<String>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = UserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvUsername.text = userList[position]
        Glide.with(holder.itemView.context)
            .load(imgAvatar[position])
            .into(holder.imgAvatar)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(userList[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: String)
    }

    class ViewHolder(private val binding: UserListBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvUsername: TextView = binding.tvUsername
        val imgAvatar: ImageView = binding.imgUserAvatar
    }
}
