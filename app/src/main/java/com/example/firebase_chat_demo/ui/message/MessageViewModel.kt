package com.example.firebase_chat_demo.ui.message

import android.app.Application
import androidx.lifecycle.*
import com.example.firebase_chat_demo.data.model.message.Chat
import com.example.firebase_chat_demo.data.model.user.User
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.utils.Constants
import com.example.firebase_chat_demo.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

class MessageViewModel(val application: Application, val userId: String) : ViewModel() {

    private var mFirebaseUser: FirebaseUser? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReferenceChatsTable: DatabaseReference? = null
    var mFriend = MutableLiveData<User>()
    var messageLiveData = MutableLiveData<String>()
    var mChats = mutableListOf<Chat>()
    var mMessagesLiveData = MutableLiveData<DataResponse<MutableList<Chat>>>()
    private var mValueEventListener: ValueEventListener? = null

    init {
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mDatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.USERS_TABLE).child(userId)
        mDatabaseReferenceChatsTable =
            FirebaseDatabase.getInstance().getReference(Constants.CHATS_TABLE)
        mMessagesLiveData.value = DataResponse.DataEmptyResponse()
    }

    fun getFriend() {
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mFriend.value = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun onSendMessage() {
        val validateType = validate()
        if (validateType == Constants.ValidateType.ValidateDone) {
            val databaseReference = FirebaseDatabase.getInstance().reference

            val mHashMap: HashMap<String, Any> = HashMap()
            mHashMap["sender"] = mFirebaseUser!!.uid
            mHashMap["receiver"] = mFriend.value!!.id
            mHashMap["message"] = messageLiveData.value!!
            mHashMap["createdAt"] = Calendar.getInstance().timeInMillis.toString()
            mHashMap["isSeen"] = "false"

            databaseReference.child(Constants.CHATS_TABLE).push().setValue(mHashMap)

            val mMessagesDatabaseReference =
                FirebaseDatabase.getInstance().getReference(Constants.MESSAGES_TABLE)
                    .child(mFirebaseUser!!.uid).child(mFriend.value!!.id)
            mMessagesDatabaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        mMessagesDatabaseReference.child("id").setValue(mFriend.value!!.id)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            messageLiveData.value = ""
        }
    }

    fun getMessage() {
        mDatabaseReferenceChatsTable!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mChats.clear()
                for (i in snapshot.children) {
                    val message = i.getValue(Chat::class.java)
                    if (message!!.sender!! == userId && message.receiver!! == FirebaseUtils.getMyUserId() ||
                        message.sender!! == FirebaseUtils.getMyUserId() && message.receiver!! == userId
                    ) {
                        mChats.add(message)
                    }
                }
                mMessagesLiveData.value = DataResponse.DataSuccessResponse(mChats.asReversed())
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    fun getSeenMessage() {
        mValueEventListener =
            mDatabaseReferenceChatsTable!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val chat = ds.getValue(Chat::class.java)
                        if (chat!!.receiver.equals(mFirebaseUser!!.uid) && chat.sender.equals(userId)) {
                            val hashMap = HashMap<String, Any>()
                            hashMap["isSeen"] = "true"
                            ds.ref.updateChildren(hashMap)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun disableValueEventListener() {
        if (mValueEventListener != null) {
            mDatabaseReferenceChatsTable!!.removeEventListener(mValueEventListener!!)
        }
    }

    val imageUrl: LiveData<String> = Transformations.map(mFriend) {
        it.imageURL
    }

    val username: LiveData<String> = Transformations.map(mFriend) {
        it.username
    }

    private fun validate(): Constants.ValidateType {
        return when (messageLiveData.value) {
            null, "" -> {
                Constants.ValidateType.EmptyMessage
            }
            else -> Constants.ValidateType.ValidateDone
        }
    }

    class Factory(private val application: Application, private val userId: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
                return MessageViewModel(application, userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}