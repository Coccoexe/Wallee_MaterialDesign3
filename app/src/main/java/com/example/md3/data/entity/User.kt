package com.example.md3.data.entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["userMail"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    val userMail: String,
    @ColumnInfo
    val userName: String,
    @ColumnInfo
    val password: String,
    @ColumnInfo
    val imageUri: Bitmap,
    @ColumnInfo
    val currency: String
)