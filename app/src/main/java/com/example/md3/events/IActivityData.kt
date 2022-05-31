package com.example.md3.events

interface IActivityData {
    fun getId(): Int
    fun getEmail(): String
    fun getUserName(): String
    fun getPassword(): String
    fun updateUser(userName: String, userId: Int)
    fun updatePassword(password: String, userMail: String)
    fun updateEmail(userMail: String, userId: Int)
    fun removeAutoLog()
}