package com.example.md3.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.md3.data.entity.AutoLogin
import com.example.md3.data.entity.Transaction
import com.example.md3.data.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutoLog(autoLogin: AutoLogin)

    @Query("DELETE FROM AutoLogin")
    suspend fun removeAutoLog()

    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction` WHERE userMail = :userMail")
    suspend fun getUserWithTransactions(userMail: String): List<Transaction>

    //login
    @Query("SELECT * FROM user WHERE userMail = :userMail and password = :password")
    suspend fun login(userMail: String, password: String) : User?

    @Query("SELECT * FROM autologin")
    suspend fun getAutoLogin() : AutoLogin?

    //id
    @Query("Select id from user where userMail = :userMail")
    suspend fun getId(userMail: String) : Int

    //UserName
    @Query("Select userName FROM user WHERE id = :id")
    suspend fun getUserName(id : Int) : String

    //email
    @Query("UPDATE user SET userMail=:userMail WHERE id = :id")
    suspend fun updateEmail(userMail: String, id: Int)

    @Query("Select userMail From user where id = :id")
    suspend fun getEmail(id: Int) : String

    //password
    @Query("UPDATE user SET password=:password WHERE id = :id")
    suspend fun updatePassword(password: String, id: Int)

    @Query("Select password From user where id = :id")
    suspend fun getPassword(id: Int) : String
}