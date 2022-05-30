package com.example.md3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.md3.data.entity.AutoLogin
import com.example.md3.data.entity.Transaction
import com.example.md3.data.entity.User

@Database(
    entities = [
        User::class,
        Transaction::class,
        AutoLogin::class
    ],
    version = 1
)
abstract class UserDatabase : RoomDatabase() {

    abstract val userDao: UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context) : UserDatabase{
            synchronized(this){
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_db"
                ).build().also{
                    INSTANCE = it
                }
            }
        }
    }

}