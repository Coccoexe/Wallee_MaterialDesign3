package com.example.md3.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    @ColumnInfo
    val category: String,
    @ColumnInfo
    val sum: Double
)