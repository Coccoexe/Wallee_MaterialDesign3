package com.example.md3.events

import android.net.Uri

interface IActivityData {
    fun getId(): Int
    fun getEmail(): String
    fun getUserName(): String
    fun getPassword(): String
    fun getImageUri(): Uri
    fun updateUser(userName: String, userId: Int)
    fun updatePassword(password: String, userMail: String)
    fun updateEmail(userMail: String, userId: Int)
    fun updateImageUri(imageUri: Uri, userId: Int)
    fun removeAutoLog()
}