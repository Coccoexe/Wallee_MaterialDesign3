package com.example.md3.data


import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.md3.data.entity.AutoLogin
import com.example.md3.data.entity.Goal
import com.example.md3.data.entity.Transaction
import com.example.md3.data.entity.User

@Dao
interface UserDao {

    //insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutoLog(autoLogin: AutoLogin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    //remove
    @Query("DELETE FROM AutoLogin")
    suspend fun removeAutoLog()

    @Query("DELETE FROM `transaction` Where id =:id ")
    suspend fun removeTransaction(id: Int)

    //transaction
    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction` WHERE userMail = :userMail")
    suspend fun getUserWithTransactions(userMail: String): List<Transaction>?

    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction` WHERE userMail = :userMail and category = (:filter)")
    suspend fun getUserWithTransactionsFiltered(userMail: String, filter: String): List<Transaction>?

    //getBalance
    @Query("Select sum(amount) as balance from `transaction` where userMail = :userMail")
    suspend fun getUserBalance(userMail: String): Double

    //login
    @Query("SELECT * FROM user WHERE userMail = :userMail and password = :password")
    suspend fun login(userMail: String, password: String) : User?

    @Query("SELECT * FROM autologin")
    suspend fun getAutoLogin() : AutoLogin?

    @Query("Select sum(amount) as balanceCategory from `transaction` where userMail = :userMail and category = :category")
    suspend fun getUserBalanceCategory(userMail: String, category: String): Double

    @Query("SELECT * FROM goal WHERE category = :category")
    suspend fun getGoalByCategory(category: String) : Goal?

    @Query("SELECT * FROM goal")
    suspend fun getAllGoal() : List<Goal>?

    //id
    @Query("Select id from user where userMail = :userMail")
    suspend fun getId(userMail: String) : Int


    //UserName
    @Query("Select userName FROM user WHERE id = :id")
    suspend fun getUserName(id : Int) : String

    @Query("UPDATE user SET userName = :userName where id = :id")
    suspend fun updateUser(userName : String, id: Int)


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


    //image
    @Query("UPDATE user SET imageUri=:imageUri WHERE id = :id")
    suspend fun updateImage(imageUri: Bitmap, id: Int)

    @Query("Select imageUri From user where id = :id ")
    suspend fun getImageUri(id: Int) : Bitmap?

}