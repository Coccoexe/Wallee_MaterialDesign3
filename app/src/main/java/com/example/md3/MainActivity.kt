package com.example.md3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.md3.data.UserDatabase
import com.example.md3.data.entity.Transaction
import com.example.md3.data.entity.User
import com.example.md3.data.relation.UserTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //navigator
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigationController) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.navigationBar).setupWithNavController(navController)

        //database
        val dao = UserDatabase.getInstance(this).userDao

        val users = listOf(
            User("ciao@prova.it","userProva","amogus") ,
            User("coccobin@gmail.it","coccoexe","frenata"),
            User("ghi8@frenata.dritta","ghiotto","forzanapoli")
        )

        val transactions = listOf(
            Transaction(1,"ciao@prova.it"),
            Transaction(2,"coccobin@gmail.it"),
            Transaction(3,"coccobin@gmail.it"),
            Transaction(4,"coccobin@gmail.it"),
            Transaction(5,"ghi8@frenata.dritta"),
            Transaction(6,"ghi8@frenata.dritta")
        )

        lifecycleScope.launch{
            users.forEach{dao.insertUser(it)}
            transactions.forEach{dao.insertTransaction(it)}

            val trans1 = dao.getUserWithTransactions("ghi8@frenata.dritta")

            Log.e("trans", trans1[0].toString())
        }

    }
}