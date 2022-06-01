package com.example.md3.events

import android.graphics.Bitmap
import android.net.Uri
import com.example.md3.data.UserDao
import com.example.md3.data.entity.Transaction

interface IActivityData {
    fun insertTransaction(transaction: Transaction)
    fun getId(): Int
    fun getEmail(): String
    fun getUserName(): String
    fun getPassword(): String
    fun getImageUri(): Bitmap?
    fun getUserWithTransaction(): List<Transaction>?
    fun updateUser(userName: String, userId: Int)
    fun updatePassword(password: String, userMail: String)
    fun updateEmail(userMail: String, userId: Int)
    fun updateImageUri(imageUri: Bitmap, userId: Int)
    fun removeAutoLog()
    fun existMail(userMail: String): Boolean
}