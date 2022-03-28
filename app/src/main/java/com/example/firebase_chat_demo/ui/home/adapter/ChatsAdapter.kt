package com.example.firebase_chat_demo.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.data.model.user.User
import com.example.firebase_chat_demo.databinding.ItemRcChatsBinding
import com.example.firebase_chat_demo.utils.FirebaseUtils

class ChatsAdapter : RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    private val userList = mutableListOf<User>()
    private var mOnClickListener: OnClickListener? = null
    private var countUnreadMessages: Int? = null

    fun setListUser(users: MutableList<User>) {
        userList.clear()
        userList.addAll(users)
        notifyItemRangeInserted(0, userList.size)
    }

    fun setUnreadMessages(count: Int) {
        countUnreadMessages = count
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val itemRcChatsBinding = ItemRcChatsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatsViewHolder(itemRcChatsBinding)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = userList.size


    inner class ChatsViewHolder(val binding: ItemRcChatsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val user = userList[position]
            binding.tvUsername.text = user.username
            if (user.imageURL == "default") {
                binding.imgAvatar.setImageResource(R.mipmap.ic_launcher)
            } else {
                Glide.with(binding.imgAvatar.context).load(user.imageURL).into(binding.imgAvatar)
            }
            if (countUnreadMessages == null || countUnreadMessages == 0) {
                binding.tvUnreadCount.visibility = View.GONE
                binding.tvLastMessage.setTextColor(
                    binding.tvLastMessage.context.resources.getColor(
                        R.color.suva_grey
                    )
                )
            } else {
                binding.tvUnreadCount.visibility = View.VISIBLE
                binding.tvUnreadCount.text = countUnreadMessages.toString()
                binding.tvLastMessage.setTextColor(
                    binding.tvLastMessage.context.resources.getColor(
                        R.color.purple_200
                    )
                )
            }
            FirebaseUtils.getLastMessage(user.id, binding.tvLastMessage)

            binding.root.setOnClickListener {
                mOnClickListener?.onItemClickedListener(user)
            }
        }
    }

    fun setListener(onClickListener: OnClickListener) {
        mOnClickListener = onClickListener
    }

    interface OnClickListener {
        fun onItemClickedListener(user: User)
    }
}