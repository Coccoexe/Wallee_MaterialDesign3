package com.example.md3

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.md3.data.UserDao
import com.example.md3.data.UserDatabase
import com.example.md3.data.entity.Transaction
import com.example.md3.data.entity.User
import com.example.md3.data.relation.UserTransaction
import com.example.md3.events.IActivityData
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity(), IActivityData {

    private lateinit var userEmail : String
    private lateinit var dao : UserDao
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //navigator
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigationController) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.navigationBar).setupWithNavController(navController)

        userEmail = intent.getStringExtra("email")!!

        //database
        dao = UserDatabase.getInstance(this).userDao
        runBlocking {
            userId = dao.getId(userEmail)
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun insertTransaction(transaction: Transaction) {
        runBlocking {
            dao.insertTransaction(transaction)
        }
    }

    override fun getId(): Int {
        return userId
    }

    override fun getEmail(): String {
        var email = ""

        runBlocking {
            email = dao.getEmail(userId)
        }

        return email
    }

    override fun getUserName(): String {
        var user = ""

        runBlocking {
            user = dao.getUserName(userId)
        }

        return user
    }

    override fun getPassword(): String{
        var pass = ""

        runBlocking {
            pass = dao.getPassword(userId)
        }

        return pass
    }

    override fun getImageUri(): Bitmap? {
        var uri : Bitmap? = null

        runBlocking {
            uri = dao.getImageUri(userId)
        }

        return uri
    }

    override fun updateUser(userName: String, userId: Int) {
        runBlocking {
            dao.updateUser(userName,userId)
        }
    }

    override fun updatePassword(password: String, userMail: String) {
        runBlocking {
            dao.updatePassword(password,userId)
        }
    }

    override fun updateEmail(userMail: String, userId: Int) {
        runBlocking {
            dao.updateEmail(userMail,userId)
        }
        userEmail = getEmail()
    }

    override fun updateImageUri(imageUri: Bitmap, userId: Int) {
        runBlocking {
            dao.updateImage(imageUri,userId)
        }
    }

    override fun removeAutoLog() {
        runBlocking {
            dao.removeAutoLog()
        }
    }

}