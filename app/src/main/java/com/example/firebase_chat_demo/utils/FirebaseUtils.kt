package com.example.firebase_chat_demo.utils

import com.google.firebase.auth.FirebaseAuth

object FirebaseUtils {

    fun getMyUserId(): String? {
        return if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().currentUser!!.uid
        } else null
    }
}