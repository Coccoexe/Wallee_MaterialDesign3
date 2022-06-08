package com.example.md3.utility

import android.graphics.Bitmap
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
    fun getCurrency(): String
    fun getUserBalance(): Double
    fun getUserWithTransaction(): List<Transaction>
    fun getUserWithTransactionFiltered(amount: String, category: String?, date : String?) : List<Transaction>
    fun getUserPositiveTransactionsByCategory() : Map<String,Double>?
    fun getUserNegativeTransactionsByCategory() : Map<String,Double>?
    fun getUserBalanceCategory(category: String) : Double
    fun getGoalByCategory(category: String): Goal?
    fun getAllGoal() : List<Goal>?
    fun updateUser(userName: String)
    fun updatePassword(password: String)
    fun updateEmail(userMail: String)
    fun updateImageUri(imageUri: Bitmap)
    fun updateCurrency(currency: String)
    fun removeAutoLog()
    fun removeSelectedTransaction(selected : ArrayList<Int>)
    fun removeSelectedGoal(selected: ArrayList<Int>)
    fun existMail(userMail: String): Boolean
    fun existGoal(category: String): Boolean

    //utility
    fun getDrawable(category : String) : Int
    fun formatMoney(num : Double) : String
}