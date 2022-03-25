package com.example.firebase_chat_demo.utils

import android.widget.TextView
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.data.model.message.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseUtils {

    fun getMyUserId(): String? {
        return if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().currentUser!!.uid
        } else null
    }

    fun getLastMessage(friendId: String, textView: TextView) {
        var lastMessage = ""
        val mFirebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference(Constants.CHATS_TABLE)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val message = ds.getValue(Chat::class.java)
                    if (mFirebaseUser != null && message != null) {
                        if (message.sender.equals(friendId) && message.receiver
                                .equals(mFirebaseUser.uid) ||
                            message.sender.equals(mFirebaseUser.uid) && message.receiver
                                .equals(friendId)
                        ) {
                            lastMessage = message.message.toString()
                        }
                    }
                }
                when (lastMessage) {
                    "" -> textView.text = ""
                    else -> textView.text = lastMessage
                }


                lastMessage = ""
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}