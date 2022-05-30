package com.example.md3.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.md3.data.entity.Transaction
import com.example.md3.data.entity.User

data class UserTransaction (
    @Embedded val user: User,
    @Relation(
        parentColumn = "userMail",
        entityColumn = "userMail"
    )

    val transaction: List<Transaction>

    )