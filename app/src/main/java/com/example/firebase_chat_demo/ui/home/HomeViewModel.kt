package com.example.firebase_chat_demo.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.example.firebase_chat_demo.data.model.user.User
import com.example.firebase_chat_demo.data.response.DataResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class HomeViewModel(val application: Application) : ViewModel() {

    private var mFirebaseUser: FirebaseUser? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var currentUserLiveData = MutableLiveData<DataResponse<User>>()

    init {
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mDatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser!!.uid)
    }

    fun getCurrentUser() {
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUserLiveData.value =
                    DataResponse.DataSuccessResponse(snapshot.getValue(User::class.java)!!)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    val userName: LiveData<String> = Transformations.map(currentUserLiveData) {
        (it as DataResponse.DataSuccessResponse).body.username
    }


    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}