package com.example.firebase_chat_demo.ui.message

import android.app.Application
import androidx.lifecycle.*
import com.example.firebase_chat_demo.data.model.message.Message
import com.example.firebase_chat_demo.data.model.user.User
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.utils.Constants
import com.example.firebase_chat_demo.utils.FirebaseUtils
import com.example.firebase_chat_demo.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap

class MessageViewModel(val application: Application, val userId: String) : ViewModel() {

    private var mFirebaseUser: FirebaseUser? = null
    var mDatabaseReference: DatabaseReference? = null
    var mFriend = MutableLiveData<User>()
    var messageLiveData = MutableLiveData<String>()
    var mMessages = mutableListOf<Message>()
    var mMessagesLiveData = MutableLiveData<DataResponse<MutableList<Message>>>()

    init {
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mDatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.USERS_TABLE).child(userId)
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
            mHashMap["receiverUsername"] = mFriend.value!!.username
            mHashMap["message"] = messageLiveData.value!!
            mHashMap["createdAt"] = Calendar.getInstance().timeInMillis.toString()

            databaseReference.child(Constants.CHATS_TABLE).push().setValue(mHashMap)
            messageLiveData.value = ""
        }
    }

    fun getMessage() {
        val mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.CHATS_TABLE)
        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mMessages.clear()
                for (i in snapshot.children) {
                    val message = i.getValue(Message::class.java)
                    if (message!!.sender!! == userId && message.receiver!! == FirebaseUtils.getMyUserId() ||
                        message.sender!! == FirebaseUtils.getMyUserId() && message.receiver!! == userId
                    ) {
                        mMessages.add(message)
                    }
                }
                mMessagesLiveData.value = DataResponse.DataSuccessResponse(mMessages.asReversed())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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