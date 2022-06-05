package com.example.md3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userMail: String,
    val amount: Double,
    val category: String,
    val date: String
){
    var isSelected = false
}