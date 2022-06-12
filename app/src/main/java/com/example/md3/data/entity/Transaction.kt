package com.example.md3.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    val userMail: String,
    @ColumnInfo
    val amount: Double,
    @ColumnInfo
    val category: String,
    @ColumnInfo
    val date: String
){
    var isSelected = false
}