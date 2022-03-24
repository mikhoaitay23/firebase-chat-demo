package com.example.firebase_chat_demo.ui.users.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.data.model.user.User
import com.example.firebase_chat_demo.databinding.ItemRcUsersBinding

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    val mUsers = mutableListOf<User>()
    lateinit var mOnClickListener: OnClickListener

    fun setUsers(users: MutableList<User>) {
        mUsers.clear()
        mUsers.addAll(users)
        notifyItemRangeInserted(0, mUsers.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val itemRcUsersBinding =
            ItemRcUsersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UsersViewHolder(itemRcUsersBinding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = mUsers.size

    inner class UsersViewHolder(val binding: ItemRcUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val user = mUsers[position]
            if (user.imageURL == "default") {
                binding.imgAvatar.setImageResource(R.mipmap.ic_launcher)
            } else {
                Glide.with(binding.imgAvatar.context).load(user.imageURL).into(binding.imgAvatar)
            }
            binding.tvUsername.text = user.username

            binding.root.setOnClickListener {
                mOnClickListener.onItemClicked(user)
            }
        }
    }

    fun setListener(onClickListener: OnClickListener) {
        mOnClickListener = onClickListener
    }

    interface OnClickListener {
        fun onItemClicked(user: User)
    }
}