package com.example.firebase_chat_demo.ui.message.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.data.model.message.Chat
import com.example.firebase_chat_demo.databinding.*
import com.example.firebase_chat_demo.utils.FirebaseUtils
import com.example.firebase_chat_demo.utils.Utils
import com.google.android.exoplayer2.ui.PlayerView

class MessageAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mMessageList = mutableListOf<Chat>()
    private lateinit var mOnFileMessageListener: OnFileMessageClickListener

    fun setMessageList(chat: Chat) {
        mMessageList.add(0, chat)
        notifyItemInserted(mMessageList.indexOf(chat))
        notifyItemRangeChanged(mMessageList.indexOf(chat) - 1, mMessageList.indexOf(chat))
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
            VIEW_TYPE_USER_OTHER_MESSAGE -> {
                val itemRcMessageOtherBinding = ItemRcMessageOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageOtherViewHolder(
                    itemRcMessageOtherBinding
                )
            }
            VIEW_TYPE_FILE_IMAGE_MY_MESSAGE -> {
                val itemRcMessageImageMeBinding = ItemRcMessageImageMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageFileImageMeHolder(itemRcMessageImageMeBinding)
            }
            VIEW_TYPE_FILE_IMAGE_OTHER_MESSAGE -> {
                val itemRcMessageImageOtherBinding = ItemRcMessageImageOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageFileImageOtherHolder(itemRcMessageImageOtherBinding)
            }
            VIEW_TYPE_FILE_VIDEO_MY_MESSAGE -> {
                val itemRcMessageFileVideoMeHolder = ItemRcMessageVideoMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageFileVideoMeHolder(itemRcMessageFileVideoMeHolder)
            }
            else -> {
                val itemRcMessageFileVideoMeBinding = ItemRcMessageVideoOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageFileVideoOtherHolder(itemRcMessageFileVideoMeBinding)
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
            VIEW_TYPE_FILE_IMAGE_MY_MESSAGE -> (holder as MessageFileImageMeHolder).bind(
                position
            )
            VIEW_TYPE_FILE_IMAGE_OTHER_MESSAGE -> (holder as MessageFileImageOtherHolder).bind(
                position,
                isContinuous
            )
            VIEW_TYPE_FILE_VIDEO_MY_MESSAGE -> (holder as MessageFileVideoMeHolder).bind(position)
            VIEW_TYPE_FILE_VIDEO_OTHER_MESSAGE -> (holder as MessageFileVideoOtherHolder).bind(
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
        return if (message.type == "text") {
            if (isMyMessage) {
                VIEW_TYPE_USER_MY_MESSAGE
            } else {
                VIEW_TYPE_USER_OTHER_MESSAGE
            }
        } else if (message.type == "image/jpeg") {
            if (isMyMessage) {
                VIEW_TYPE_FILE_IMAGE_MY_MESSAGE
            } else {
                VIEW_TYPE_FILE_IMAGE_OTHER_MESSAGE
            }
        } else if (message.type == "video/mp4") {
            if (isMyMessage) {
                VIEW_TYPE_FILE_VIDEO_MY_MESSAGE
            } else {
                VIEW_TYPE_FILE_VIDEO_OTHER_MESSAGE
            }
        } else {
            VIEW_TYPE_FILE_AUDIO_MY_MESSAGE
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
            if (position == 0) {
                binding.imgStatus.visibility = View.VISIBLE
                if (message.seen == "true") {
                    binding.imgStatus.setImageResource(R.drawable.ic_seen)
                } else {
                    binding.imgStatus.setImageResource(R.drawable.ic_delivered)
                }
            } else {
                binding.imgStatus.visibility = View.GONE
            }
        }
    }

    inner class MessageOtherViewHolder(val binding: ItemRcMessageOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, isContinuous: Boolean) {
            val message = mMessageList[position]
            if (isContinuous) {
                binding.imgImageProfile.visibility = View.INVISIBLE
            } else {
                binding.imgImageProfile.visibility = View.VISIBLE
//                Utils.displayRoundImageFromUrl(
//                    binding.imgImageProfile.context,
//                    message.sender.profileUrl,
//                    binding.imgImageProfile
//                )
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

    inner class MessageFileImageMeHolder(val binding: ItemRcMessageImageMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            position: Int
        ) {
            val message = mMessageList[position]
            binding.tvChatTime.text = Utils.formatTime(message.createdAt!!.toLong())
            if (position == 0) {
                binding.imgStatus.visibility = View.VISIBLE
                if (message.seen == "true") {
                    Glide.with(binding.imgStatus).load(R.drawable.ic_seen).into(binding.imgStatus)
                } else {
                    Glide.with(binding.imgStatus).load(R.drawable.ic_delivered)
                        .into(binding.imgStatus)
                }
            } else {
                binding.imgStatus.visibility = View.GONE
            }
            Glide.with(binding.imgImageFile.context).load(message.message)
                .into(binding.imgImageFile)
        }
    }

    inner class MessageFileImageOtherHolder(val binding: ItemRcMessageImageOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, isContinuous: Boolean) {
            val message = mMessageList[position]
            if (isContinuous) {
                binding.imgImageProfile.visibility = View.INVISIBLE
            } else {
                binding.imgImageProfile.visibility = View.VISIBLE
            }
            binding.tvChatTime.text = Utils.formatTime(message.createdAt!!.toLong())
            Glide.with(binding.imgImageFile.context).load(message.message)
                .into(binding.imgImageFile)
        }
    }

    inner class MessageFileVideoMeHolder(val binding: ItemRcMessageVideoMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val message = mMessageList[position]
            binding.tvChatTime.text = Utils.formatTime(message.createdAt!!.toLong())
            Glide.with(binding.imgFileThumbnail.context).load(R.drawable.img_default_thumbnail)
                .into(binding.imgFileThumbnail)
            if (position == 0) {
                binding.imgStatus.visibility = View.VISIBLE
                if (message.seen == "true") {
                    Glide.with(binding.imgStatus).load(R.drawable.ic_seen).into(binding.imgStatus)
                } else {
                    Glide.with(binding.imgStatus).load(R.drawable.ic_delivered)
                        .into(binding.imgStatus)
                }
            } else {
                binding.imgStatus.visibility = View.GONE
            }
            binding.imgStart.setOnClickListener {
                mOnFileMessageListener.onFileMessageClicked(binding.imgSurfaceVideo, message)
                binding.imgStart.visibility = View.GONE
            }
        }
    }

    inner class MessageFileVideoOtherHolder(val binding: ItemRcMessageVideoOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, isContinuous: Boolean) {
            val message = mMessageList[position]
            binding.tvChatTime.text = Utils.formatTime(message.createdAt!!.toLong())
            if (isContinuous) {
                binding.imgImageProfile.visibility = View.INVISIBLE
                binding.tvChatTime.visibility = View.GONE
            } else {
                binding.imgImageProfile.visibility = View.VISIBLE
            }
            binding.imgPlay.setOnClickListener {
                mOnFileMessageListener.onFileMessageClicked(binding.imgSurfaceVideo, message)
                binding.imgPlay.visibility = View.GONE
            }
        }
    }

    private fun isContinuous(currentChat: Chat?, precedingChat: Chat?): Boolean {
        if (currentChat == null || precedingChat == null) {
            return false
        }
        val currentUserId: String? = currentChat.sender
        val precedingUserId: String? = precedingChat.sender

        return if (currentUserId == null || precedingUserId == null) {
            false
        } else currentUserId == precedingUserId
    }

    fun setFileMessageClickListener(mOnFileMessageClickListener: OnFileMessageClickListener) {
        mOnFileMessageListener = mOnFileMessageClickListener
    }

    interface OnFileMessageClickListener {
        fun onFileMessageClicked(playerView: PlayerView, chat: Chat)
    }

    companion object {
        private const val VIEW_TYPE_USER_MY_MESSAGE = 10
        private const val VIEW_TYPE_USER_OTHER_MESSAGE = 11
        private const val VIEW_TYPE_FILE_IMAGE_MY_MESSAGE = 12
        private const val VIEW_TYPE_FILE_IMAGE_OTHER_MESSAGE = 13
        private const val VIEW_TYPE_FILE_VIDEO_MY_MESSAGE = 14
        private const val VIEW_TYPE_FILE_VIDEO_OTHER_MESSAGE = 15
        private const val VIEW_TYPE_FILE_AUDIO_MY_MESSAGE = 16
        private const val VIEW_TYPE_FILE_AUDIO_OTHER_MESSAGE = 17
    }
}