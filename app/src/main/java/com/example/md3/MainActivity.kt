package com.example.md3

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


    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun getEmail(): String {
        return userEmail
    }

    override fun getUserName(): String {
        var user = ""

        runBlocking {
            user = dao.getUserName(userEmail)
        }

        return user
    }

    override fun removeAutoLog() {
        runBlocking {
            dao.removeAutoLog()
        }
    }

}