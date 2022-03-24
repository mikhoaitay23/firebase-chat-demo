package com.example.firebase_chat_demo.ui.signup

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupViewModel(val application: Application) : ViewModel() {

    var userNameLiveData = MutableLiveData<String>()
    var emailLiveData = MutableLiveData<String>()
    var passwordLiveData = MutableLiveData<String>()

    private var mFirebaseAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null

    val saveDoneLiveData = MutableLiveData<DataResponse<Constants.ValidateType>>()
    val signUpLiveData = MutableLiveData<DataResponse<Boolean>>()

    init {
        mFirebaseAuth = FirebaseAuth.getInstance()
        signUpLiveData.value = DataResponse.DataEmptyResponse()
    }

    fun onSignup() {
        val validateType = validate()
        if (validateType == Constants.ValidateType.ValidateDone) {
            mFirebaseAuth!!.createUserWithEmailAndPassword(
                emailLiveData.value!!,
                passwordLiveData.value!!
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val mFirebaseUser = mFirebaseAuth!!.currentUser
                        val userId = mFirebaseUser!!.uid
                        mDatabaseReference =
                            FirebaseDatabase.getInstance().getReference(Constants.USERS_TABLE).child(userId)

                        val mHashMap: HashMap<String, String> = HashMap()
                        mHashMap["id"] = userId
                        mHashMap["username"] = userNameLiveData.value!!
                        mHashMap["imageURL"] = "default"

                        mDatabaseReference!!.setValue(mHashMap).addOnCompleteListener { p0 ->
                            if (p0.isSuccessful) {
                                signUpLiveData.value = DataResponse.DataSuccessResponse(true)
                            }
                        }
                    } else {
                        signUpLiveData.value = DataResponse.DataSuccessResponse(false)
                    }
                }
        }
        saveDoneLiveData.value = DataResponse.DataSuccessResponse(validateType)
    }

    private fun validate(): Constants.ValidateType {
        return when {
            userNameLiveData.value == null -> {
                Constants.ValidateType.EmptyUserName
            }
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
            if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
                return SignupViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}