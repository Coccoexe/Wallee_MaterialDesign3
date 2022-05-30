package com.example.md3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userMail: String

)