package com.example.firebase_chat_demo.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseUtils {

    private var mFirebaseAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null

    init {
        mFirebaseAuth = FirebaseAuth.getInstance()
    }

    fun onRegister(
        userName: String,
        email: String,
        password: String,
        mOnRegisterListener: OnRegisterListener
    ) {
        mFirebaseAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mFirebaseUser = mFirebaseAuth!!.currentUser
                    val userId = mFirebaseUser!!.uid
                    mDatabaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    var mHashMap: HashMap<String, String> = HashMap()
                    mHashMap["id"] = userId
                    mHashMap["username"] = userName
                    mHashMap["imageURL"] = "default"

                    mDatabaseReference!!.setValue(mHashMap).addOnCompleteListener { p0 ->
                        if (p0.isSuccessful) {
                            mOnRegisterListener.onRegisterSucceed()
                        }
                    }
                } else {
                    mOnRegisterListener.onRegisterFailed()
                }
            }
    }

    fun onSignIn(email: String, password: String, mOnSignInListener: OnSignInListener) {
        mFirebaseAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    mOnSignInListener.onSignInSucceed()
                } else {
                    mOnSignInListener.onSignInFailed()
                }
            }
    }

    interface OnRegisterListener {
        fun onRegisterSucceed()
        fun onRegisterFailed()
    }

    interface OnSignInListener {
        fun onSignInSucceed()
        fun onSignInFailed()
    }
}