package com.example.firebase_chat_demo.data.model.message

data class Chat(
    var sender: String? = "",
    var receiver: String? = "",
    var message: String? = "",
    var createdAt: String? = "",
    var seen: String? = "",
    var type: String? = ""
)
