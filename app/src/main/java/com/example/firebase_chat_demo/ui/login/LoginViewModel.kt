package com.example.firebase_chat_demo.ui.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(val application: Application) : ViewModel() {

    var emailLiveData = MutableLiveData<String>()
    var passwordLiveData = MutableLiveData<String>()
    var validateLiveData = MutableLiveData<DataResponse<Constants.ValidateType>>()
    var onLoginLiveData = MutableLiveData<DataResponse<Boolean>>()

    private var mFirebaseAuth: FirebaseAuth? = null

    init {
        mFirebaseAuth = FirebaseAuth.getInstance()
    }

    fun onLogin() {
        val validateType = validate()
        if (validateType == Constants.ValidateType.ValidateDone) {
            mFirebaseAuth!!.signInWithEmailAndPassword(
                emailLiveData.value!!,
                passwordLiveData.value!!
            )
                .addOnCompleteListener { p0 ->
                    if (p0.isSuccessful) {
                        onLoginLiveData.value = DataResponse.DataSuccessResponse(true)
                    } else {
                        onLoginLiveData.value = DataResponse.DataSuccessResponse(false)
                    }
                }
        }
        validateLiveData.value = DataResponse.DataSuccessResponse(validateType)
    }

    private fun validate(): Constants.ValidateType {
        return when {
            emailLiveData.value == null -> {
                Constants.ValidateType.EmptyEmail
            }
            passwordLiveData.value == null -> {
                Constants.ValidateType.EmptyPassword
            }
            passwordLiveData.value!!.length < 6 -> {
                Constants.ValidateType.InvalidPassword
            }
            else -> Constants.ValidateType.ValidateDone
        }
    }

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}