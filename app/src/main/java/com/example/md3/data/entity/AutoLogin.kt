package com.example.md3.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AutoLogin(
    @PrimaryKey(autoGenerate = false)
    val userMail: String,
    @ColumnInfo
    val password: String
)