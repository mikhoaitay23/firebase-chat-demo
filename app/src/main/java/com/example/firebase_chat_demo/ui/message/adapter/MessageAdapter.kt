package com.example.firebase_chat_demo.ui.message.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_chat_demo.data.model.message.Message
import com.example.firebase_chat_demo.databinding.ItemRcMessageMeBinding
import com.example.firebase_chat_demo.databinding.ItemRcMessageOtherBinding
import com.example.firebase_chat_demo.utils.FirebaseUtils
import com.example.firebase_chat_demo.utils.Utils

class MessageAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mMessageList = mutableListOf<Message>()

    fun setMessageList(messages: MutableList<Message>) {
        mMessageList.clear()
        mMessageList.addAll(messages)
        notifyItemRangeInserted(0, mMessageList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER_MY_MESSAGE -> {
                val itemRcMessageMeBinding = ItemRcMessageMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageMeViewHolder(
                    itemRcMessageMeBinding
                )
            }
            else -> {
                val itemRcMessageOtherBinding = ItemRcMessageOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageOtherViewHolder(
                    itemRcMessageOtherBinding
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = mMessageList[position]
        var isContinuous = false
        if (position < mMessageList.size - 1) {
            val prevMessage = mMessageList[position + 1]
            isContinuous =
                if (!Utils.hasSameDate(
                        currentMessage.createdAt!!.toLong(),
                        prevMessage.createdAt!!.toLong()
                    )
                ) {
                    false
                } else {
                    isContinuous(currentMessage, prevMessage)
                }
        }
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MY_MESSAGE -> (holder as MessageMeViewHolder).bind(
                position,
                isContinuous
            )
            VIEW_TYPE_USER_OTHER_MESSAGE -> (holder as MessageOtherViewHolder).bind(
                position,
                isContinuous
            )
        }
    }

    override fun getItemCount() = mMessageList.size

    override fun getItemViewType(position: Int): Int {
        val message = mMessageList[position]
        var isMyMessage = false
        if (message.sender == FirebaseUtils.getMyUserId()) {
            isMyMessage = true
        }
        return if (isMyMessage) {
            VIEW_TYPE_USER_MY_MESSAGE
        } else {
            VIEW_TYPE_USER_OTHER_MESSAGE
        }
    }

    inner class MessageMeViewHolder(val binding: ItemRcMessageMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, isContinuous: Boolean) {
            val message = mMessageList[position]
            binding.tvChatMessage.text = message.message
            binding.tvChatTime.text = Utils.formatTime(message.createdAt!!.toLong())
//            if (message.updatedAt > 0) {
//                binding.tvEdited.visibility = View.VISIBLE
//            } else {
            binding.tvEdited.visibility = View.GONE
//            }

            if (isContinuous) {
                binding.mViewChatPadding.visibility = View.GONE
            } else {
                binding.mViewChatPadding.visibility = View.VISIBLE
            }
            binding.mLayoutUrlPreview.visibility = View.GONE
        }
    }

    inner class MessageOtherViewHolder(val binding: ItemRcMessageOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, isContinuous: Boolean) {
            val message = mMessageList[position]
            if (isContinuous) {
                binding.imgImageProfile.visibility = View.INVISIBLE
                binding.tvNickname.visibility = View.GONE
            } else {
                binding.imgImageProfile.visibility = View.VISIBLE
//                Utils.displayRoundImageFromUrl(
//                    binding.imgImageProfile.context,
//                    message.sender.profileUrl,
//                    binding.imgImageProfile
//                )
                binding.tvNickname.visibility = View.VISIBLE
                binding.tvNickname.text = message.receiverUsername
            }
            binding.tvChatMessage.text = message.message
            binding.tvChatTime.text = Utils.formatTime(message.createdAt!!.toLong())
//            if (message.updatedAt > 0) {
//                binding.tvEdited.visibility = View.VISIBLE
//            } else {
            binding.tvEdited.visibility = View.GONE
//            }
            binding.mLayoutUrlPreview.visibility = View.GONE
        }
    }

    private fun isContinuous(currentMessage: Message?, precedingMessage: Message?): Boolean {
        if (currentMessage == null || precedingMessage == null) {
            return false
        }
        val currentUserId: String? = currentMessage.sender
        val precedingUserId: String? = precedingMessage.sender

        return if (currentUserId == null || precedingUserId == null) {
            false
        } else currentUserId == precedingUserId
    }

    companion object {
        private const val VIEW_TYPE_USER_MY_MESSAGE = 10
        private const val VIEW_TYPE_USER_OTHER_MESSAGE = 11
    }
}