package com.example.firebase_chat_demo.ui.users

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebase_chat_demo.data.model.user.User
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UsersViewModel(val application: Application) : ViewModel() {

    var usersLiveData = MutableLiveData<DataResponse<MutableList<User>>>()
    var users = mutableListOf<User>()
    var mFirebaseUser: FirebaseUser? = null
    var mDatabaseReference: DatabaseReference? = null

    init {
        usersLiveData.value = DataResponse.DataEmptyResponse()
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.USERS_TABLE)
    }

    fun getUsers() {
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (i in snapshot.children){
                    val user = i.getValue(User::class.java)
                    if (user != null && user.id != mFirebaseUser?.uid){
                        users.add(user)
                    }
                }
                usersLiveData.value = DataResponse.DataSuccessResponse(users)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
                return UsersViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}