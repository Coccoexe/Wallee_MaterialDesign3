package com.example.md3.data

import android.content.Context
import androidx.room.*
import com.example.md3.data.converter.ImageConverter
import com.example.md3.data.entity.AutoLogin
import com.example.md3.data.entity.Goal
import com.example.md3.data.entity.Transaction
import com.example.md3.data.entity.User

@Database(
    entities = [
        User::class,
        Transaction::class,
        AutoLogin::class,
        Goal::class
    ],
    version = 1
)
@TypeConverters(ImageConverter::class)
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