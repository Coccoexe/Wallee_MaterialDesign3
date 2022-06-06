package com.example.md3.events

import android.graphics.Bitmap
import android.net.Uri
import com.example.md3.data.UserDao
import com.example.md3.data.entity.Goal
import com.example.md3.data.entity.Transaction

interface IActivityData {
    fun insertTransaction(transaction: Transaction)
    fun insertGoal(goal: Goal)
    fun getId(): Int
    fun getEmail(): String
    fun getUserName(): String
    fun getPassword(): String
    fun getImageUri(): Bitmap?
    fun getUserBalance(): Double
    fun getUserWithTransaction(): List<Transaction>?
    fun getUserWithTransactionFiltered(amount: String, category: String?, date : String?) : List<Transaction>?
    fun getUserBalanceCategory(category: String) : Double
    fun getGoalByCategory(category: String): Goal?
    fun getAllGoal() : List<Goal>?
    fun updateUser(userName: String, userId: Int)
    fun updatePassword(password: String, userMail: String)
    fun updateEmail(userMail: String, userId: Int)
    fun updateImageUri(imageUri: Bitmap, userId: Int)
    fun removeAutoLog()
    fun removeSelectedTransaction(selected : ArrayList<Int>)
    fun existMail(userMail: String): Boolean

    //utility
    fun getDrawable(category : String) : Int
    fun formatMoney(num : Double) : String
}