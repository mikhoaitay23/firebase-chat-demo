package com.example.firebase_chat_demo.utils

object Constants {

    enum class ValidateType {
        ValidateDone, EmptyUserName, EmptyEmail, EmptyPassword, EmptyMessage, InvalidPassword
    }

    const val USERS_TABLE = "Users"
    const val CHATS_TABLE = "Chats"
    const val MESSAGES_TABLE = "Messages"
}