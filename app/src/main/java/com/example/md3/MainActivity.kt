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
import com.example.md3.data.entity.Goal
import com.example.md3.data.entity.Transaction
import com.example.md3.data.entity.User
import com.example.md3.data.relation.UserTransaction
import com.example.md3.events.IActivityData
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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

    override fun insertGoal(goal: Goal) {
        runBlocking {
            dao.insertGoal(goal)
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

    override fun getUserBalance(): Double {
        var balance : Double? = null
        runBlocking {
            balance = dao.getUserBalance(userEmail)
        }
        if (balance == null) {
            balance = 0.0
        }
        return balance!!
    }

    override fun getUserBalanceCategory(category: String): Double {
        var balance : Double? = null
        runBlocking {
            balance = dao.getUserBalanceCategory(userEmail, category)
        }
        if (balance == null) {
            balance = 0.0
        }

        return balance!!
    }

    override fun getGoalByCategory(category: String): Goal? {
        var goal : Goal? = null
        runBlocking {
            goal = dao.getGoalByCategory(category)
        }

        return goal
    }

    override fun getAllGoal(): List<Goal>? {
        var listGoal : List<Goal>? = null
        runBlocking {
            listGoal = dao.getAllGoal()
        }

        return listGoal
    }


    override fun getUserWithTransaction(): List<Transaction>? {
        var transactionList : List<Transaction>? = null

        runBlocking {
            transactionList = dao.getUserWithTransactions(userEmail)
        }

        return transactionList
    }

    override fun getUserWithTransactionFiltered(amount: String, category: String?, date: String?): List<Transaction>? {
        var transactionList : List<Transaction>? = null
        val ret : ArrayList<Transaction> = ArrayList()
        val format : SimpleDateFormat = SimpleDateFormat("EE d MMM yyyy", Locale.getDefault())

        runBlocking {
            if (category == null){
                transactionList = dao.getUserWithTransactions(userEmail)
            }
            else {
                transactionList = dao.getUserWithTransactionsFiltered(userEmail, category)
            }
        }

        if (!transactionList.isNullOrEmpty()) {
            when (amount) {
                "all" -> {
                    for (t in transactionList!!){
                        if (date == null || format.parse(t.date)!! > format.parse(date))
                        {
                            ret.add(t)
                        }
                    }
                }
                "positive" -> {
                    for (t in transactionList!!){
                        if (t.amount > 0.0)
                        {
                            if (date == null || format.parse(t.date)!! > format.parse(date))
                            {
                                ret.add(t)
                            }
                        }
                    }
                }
                "negative" -> {
                    for (t in transactionList!!){
                        if (t.amount < 0.0)
                        {
                            if (date == null || format.parse(t.date)!! > format.parse(date))
                            {
                                ret.add(t)
                            }
                        }
                    }
                }
            }
        }

        return ret
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

    override fun removeSelectedTransaction(selected : ArrayList<Int>) {
        runBlocking {
            for (t in selected) {
                dao.removeTransaction(t)
            }
        }
    }

    override fun existMail(userMail: String) : Boolean{
        var exist : Int? = null
        runBlocking {
            exist = dao.getId(userMail)
        }
        return exist != null
    }

}