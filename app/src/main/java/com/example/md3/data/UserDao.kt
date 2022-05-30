package com.example.md3.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.md3.data.entity.Transaction
import com.example.md3.data.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction` WHERE userMail = :userMail")
    suspend fun getUserWithTransactions(userMail: String): List<Transaction>

}