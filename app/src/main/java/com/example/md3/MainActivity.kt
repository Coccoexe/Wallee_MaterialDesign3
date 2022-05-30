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

        /*
        //database
        val dao = UserDatabase.getInstance(this).userDao

        val users = listOf(
            User("ciao@prova.it","userProva","amogus",0) ,
            User("coccobin@gmail.it","coccoexe","frenata",0),
            User("ghi8@frenata.dritta","ghiotto","forzanapoli",0)
        )

        val transactions = listOf(
            Transaction(0,"ciao@prova.it",10,"lavaggio_macchina"),
            Transaction(0,"coccobin@gmail.it",10,"ripetizioni"),
            Transaction(0,"coccobin@gmail.it",-30,"cena_la_marghe"),
            Transaction(0,"coccobin@gmail.it",40,"lavorato_viale_verona"),
            Transaction(0,"ghi8@frenata.dritta",-840,"pc_nuovo"),
            Transaction(0,"ghi8@frenata.dritta",900,"gratta&vinci")
        )

        lifecycleScope.launch{
            users.forEach{dao.insertUser(it)}
            transactions.forEach{dao.insertTransaction(it)}

            val trans1 = dao.getUserWithTransactions("ghi8@frenata.dritta")

            Log.e("trans", trans1[0].toString())
        }
        */
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}