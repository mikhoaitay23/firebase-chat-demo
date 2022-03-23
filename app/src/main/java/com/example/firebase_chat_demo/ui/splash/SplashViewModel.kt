package com.example.firebase_chat_demo.ui.splash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebase_chat_demo.data.response.DataResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashViewModel(val application: Application) : ViewModel() {

    var mFirebaseUser: FirebaseUser? = null
    var isCurrentUser = MutableLiveData<DataResponse<Boolean>>()

    init {
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        isCurrentUser.value = DataResponse.DataEmptyResponse()
    }

    fun getCurrentUser(){
        if (mFirebaseUser == null){
            isCurrentUser.value = DataResponse.DataSuccessResponse(false)
        } else {
            isCurrentUser.value = DataResponse.DataSuccessResponse(true)
        }
    }

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
                return SplashViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}