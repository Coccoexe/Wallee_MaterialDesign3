package com.example.md3.events

interface IActivityData {
    fun getEmail(): String
    fun getUserName(): String
    fun getPassword(): String
    fun updatePassword(password: String, userMail: String)
    fun removeAutoLog()
}