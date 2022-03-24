package com.example.firebase_chat_demo.data.model.message

data class Message(
    var sender: String? = "",
    var receiver: String? = "",
    var receiverUsername: String? = "",
    var message: String? = "",
    var createdAt: String? = ""
)
