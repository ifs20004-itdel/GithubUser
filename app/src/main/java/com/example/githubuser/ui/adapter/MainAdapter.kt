package com.example.githubuser.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubuser.data.local.entity.FavoriteUser
import com.example.githubuser.databinding.UserListBinding

class MainAdapter : ListAdapter<FavoriteUser, MainAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = UserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val fUser = getItem(position)
        holder.bind(fUser)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(fUser.username, fUser.avatarUrl)
        }
    }

    class MyViewHolder(
        private val binding: UserListBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(favorite: FavoriteUser) {
            binding.tvUsername.text = favorite.username
            Glide.with(itemView.context)
                .load(favorite.avatarUrl)
                .into(binding.imgUserAvatar)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(login: String, url: String?)
    }
    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<FavoriteUser> =
            object : DiffUtil.ItemCallback<FavoriteUser>() {
                override fun areItemsTheSame(
                    oldUser: FavoriteUser,
                    newUser: FavoriteUser
                ): Boolean {
                    return oldUser.username == newUser.username
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldUser: FavoriteUser,
                    newUser: FavoriteUser
                ): Boolean {
                    return oldUser == newUser
                }
            }
    }
}
