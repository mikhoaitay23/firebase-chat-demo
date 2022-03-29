package com.example.firebase_chat_demo.ui.message

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.*
import com.example.firebase_chat_demo.data.model.message.Chat
import com.example.firebase_chat_demo.data.model.user.User
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.utils.Constants
import com.example.firebase_chat_demo.utils.FirebaseUtils
import com.example.firebase_chat_demo.utils.MediaPlayerUtils
import com.example.firebase_chat_demo.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.launch
import java.util.*

class MessageViewModel(val application: Application, val userId: String) : ViewModel() {

    private var mFirebaseUser: FirebaseUser? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReferenceChatsTable: DatabaseReference? = null
    var mFriend = MutableLiveData<User>()
    var messageLiveData = MutableLiveData<String>()
    var mMessagesLiveData = MutableLiveData<DataResponse<Chat>>()
    var mMessageChangesLiveData = MutableLiveData<DataResponse<Chat>>()
    var mMediaPlayerLiveData = MutableLiveData<DataResponse<MediaPlayer>>()
    private var mValueEventListener: ValueEventListener? = null
    private val mediaPlayerUtils = MediaPlayerUtils()
    private var mediaPlayer = MediaPlayer()

    init {
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mDatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constants.USERS_TABLE).child(userId)
        mDatabaseReferenceChatsTable =
            FirebaseDatabase.getInstance().getReference(Constants.CHATS_TABLE)
        mMessagesLiveData.value = DataResponse.DataEmptyResponse()
        mMessageChangesLiveData.value = DataResponse.DataEmptyResponse()
        mMediaPlayerLiveData.value = DataResponse.DataEmptyResponse()
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
            mHashMap["seen"] = "false"
            mHashMap["type"] = "text"

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

    fun onSendMediaFile(uri: Uri) {
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().reference.child("Media files")
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child(Constants.CHATS_TABLE).push()

        val filePath: StorageReference = storageReference.child(databaseReference.key.toString())

        val uploadTask: UploadTask = filePath.putFile(uri)
        uploadTask.continueWithTask { p0 ->
            if (!p0.isSuccessful) {
                p0.exception
            }
            filePath.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                val downloadUri = it.result
                val mHashMap: HashMap<String, Any> = HashMap()
                mHashMap["sender"] = mFirebaseUser!!.uid
                mHashMap["receiver"] = mFriend.value!!.id
                mHashMap["message"] = downloadUri.toString()
                mHashMap["createdAt"] = Calendar.getInstance().timeInMillis.toString()
                mHashMap["seen"] = "false"
                mHashMap["type"] = Utils.getTypeMediaFromUri(application, uri)

                databaseReference.setValue(mHashMap)

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
            }
        }
    }

    fun getMessage() {
        mDatabaseReferenceChatsTable!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Chat::class.java)
                if (message!!.sender!! == userId && message.receiver!! == FirebaseUtils.getMyUserId() ||
                    message.sender!! == FirebaseUtils.getMyUserId() && message.receiver!! == userId
                ) {
                    mMessagesLiveData.value = DataResponse.DataSuccessResponse(message)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chat = snapshot.getValue(Chat::class.java)
                mMessageChangesLiveData.value = DataResponse.DataSuccessResponse(chat!!)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

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
                            hashMap["seen"] = "true"
                            ds.ref.updateChildren(hashMap)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun onStartMediaPlayer(url: String){
        viewModelScope.launch {
            mediaPlayerUtils.onPlay(mediaPlayer, url)
            mMediaPlayerLiveData.value = DataResponse.DataSuccessResponse(mediaPlayer)
        }
    }

    fun onPauseMediaPlayer(){
        viewModelScope.launch {
            mediaPlayerUtils.onPause(mediaPlayer)
            mMediaPlayerLiveData.value = DataResponse.DataSuccessResponse(mediaPlayer)
        }
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