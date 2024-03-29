package com.example.md3.activity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.md3.R
import com.example.md3.data.UserDao
import com.example.md3.data.UserDatabase
import com.example.md3.data.entity.Goal
import com.example.md3.data.entity.Transaction
import com.example.md3.utility.IActivityData
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*


class MainActivity : AppCompatActivity(), IActivityData {

    private lateinit var userEmail : String
    private lateinit var dao : UserDao
    private lateinit var goalBadge : BadgeDrawable
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //navigator
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigationController) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.navigationBar).setupWithNavController(navController)

        //ricevo dal login l'utente che ha effettuato l'accesso
        userEmail = intent.getStringExtra("email")!!

        //database
        //ottengo l'id dell'utente dopo aver ottenuto un istanza del database
        //che verra' utilizzato per tutto il tempo in cui l'app rimarra' aperta
        dao = UserDatabase.getInstance(this).userDao
        runBlocking {
            userId = dao.getId(userEmail)
        }

        //badge
        val navigationBar : BottomNavigationView = findViewById(R.id.navigationBar)
        goalBadge = navigationBar.getOrCreateBadge(R.id.goalFragment)
        updateBadge()

    }

    //voglio impedire che l'utente possa usare la gesture back
    //perche' per navigare tra i fragment e' presenta la navigation bar
    //e altrimenti ritornerebbe alla pagina di login(esiste il pulsante apposito per uscire)
    override fun onBackPressed() {
        //super.onBackPressed()
    }

    //implementazione di tutti i metodi di Utility usati da tutti i fragment
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
        lateinit var email : String

        runBlocking {
            email = dao.getEmail(userId)
        }

        return email
    }

    override fun getUserName(): String {
        lateinit var user : String

        runBlocking {
            user = dao.getUserName(userId)
        }

        return user
    }

    override fun getPassword(): String{
        lateinit var pass :String

        runBlocking {
            pass = dao.getPassword(userId)
        }

        return pass
    }

    override fun getImageUri(): Bitmap? {
        var uri: Bitmap?

        runBlocking {
            uri = dao.getImageUri(userId)
        }

        return uri
    }

    override fun getCurrency(): String {
        var currency : String

        runBlocking {
            currency = dao.getCurrency(userId)
        }

        return currency
    }

    override fun getUserBalance(): Double {
        var balance: Double?
        runBlocking {
            balance = dao.getUserBalance(userEmail)
        }
        if (balance == null) {
            balance = 0.0
        }
        return balance!!
    }

    override fun getUserBalanceCategory(amount: String, category: String, date: String): Double {
        val balanceCategory = getUserWithTransactionFiltered(amount, category, date)

        return balanceCategory.sumOf { it.amount }
    }

    override fun getGoalByCategory(category: String,date: String, amount: String): Goal? {
        var goal: Goal?
        runBlocking {
            goal = when(amount){
                "positive" -> {
                    dao.getPositiveGoalByCategory(userEmail,category,date)
                }
                "negative" -> {
                    dao.getNegativeGoalByCategory(userEmail,category,date)
                }
                else -> null
            }
        }

        return goal
    }

    override fun getAllGoal(amount: String): List<Goal> {
        var listGoal: List<Goal>?
        val ret : ArrayList<Goal> = ArrayList()

        runBlocking {
            listGoal = dao.getAllGoal(userEmail)
        }

        if (!listGoal.isNullOrEmpty()) {
            when (amount) {
                "all" -> {
                    for (g in listGoal!!){
                        ret.add(g)
                    }
                }
                "positive" -> {
                    for (g in listGoal!!){
                        if (g.sum > 0.0)
                            ret.add(g)
                    }
                }
                "negative" -> {
                    for (g in listGoal!!){
                        if (g.sum < 0.0)
                            ret.add(g)
                    }
                }
            }
        }

        return ret
    }

    override fun getUserWithTransaction(): List<Transaction> {
        var transactionList: List<Transaction>

        runBlocking {
            transactionList = dao.getUserWithTransactions(userEmail)
        }

        return transactionList
    }

    override fun getUserWithTransactionFiltered(amount: String, category: String?, date: String?): List<Transaction> {
        var transactionList: List<Transaction>?
        val ret : ArrayList<Transaction> = ArrayList()
        val format = SimpleDateFormat("EE d MMM yyyy", Locale.getDefault())

        runBlocking {
            transactionList = if (category == null){
                dao.getUserWithTransactions(userEmail)
            } else {
                dao.getUserWithTransactionsFiltered(userEmail, category)
            }
        }

        if (!transactionList.isNullOrEmpty()) {
            when (amount) {
                "all" -> {
                    for (t in transactionList!!){
                        if (date == null || format.parse(t.date)!! >= format.parse(date))
                        {
                            ret.add(t)
                        }
                    }
                }
                "positive" -> {
                    for (t in transactionList!!){
                        if (t.amount > 0.0)
                        {
                            if (date == null || format.parse(t.date)!! >= format.parse(date))
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
                            if (date == null || format.parse(t.date)!! >= format.parse(date))
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

    override fun updateUser(userName: String) {
        runBlocking {
            dao.updateUser(userName,userId)
        }
    }

    override fun updatePassword(password: String) {
        runBlocking {
            dao.updatePassword(password,userId)
        }
    }

    override fun updateEmail(userMail: String) {
        runBlocking {
            dao.updateEmail(userMail,userId)
        }
        userEmail = getEmail()
    }

    override fun updateImageUri(imageUri: Bitmap) {
        runBlocking {
            dao.updateImage(imageUri,userId)
        }
    }

    override fun updateCurrency(currency: String) {
        runBlocking {
            dao.updateCurrency(currency,userId)
        }
    }

    override fun setCompletedGoal(completed: Boolean, id : Int) {
        runBlocking {
            dao.setCompletedGoal(completed,id)
        }
    }

    override fun setNotified(toNotify : Boolean, id: Int) {
        runBlocking {
            dao.setNotified(toNotify,id)
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

    override fun removeSelectedGoal(selected: ArrayList<Int>) {
        runBlocking {
            for (t in selected) {
                dao.removeGaol(t)
            }
        }
    }

    override fun existMail(userMail: String) : Boolean{
        var exist: Int?
        runBlocking {
            exist = dao.getId(userMail)
        }
        return exist != null
    }

    override fun existGoal(category: String, date: String, amount: String): Boolean {
        val exist: Goal?
        runBlocking {
            exist = when(amount){
                "positive" -> {
                    dao.getPositiveGoalByCategory(userEmail,category,date)
                }
                "negative" -> {
                    dao.getNegativeGoalByCategory(userEmail,category,date)
                }
                else -> null
            }
        }
        return exist != null
    }

    override fun getDrawable(category: String): Int {
        val income = resources.getStringArray(R.array.income)
        val expense = resources.getStringArray(R.array.expenses)
        when(category){
            income[0] -> return R.drawable.salary
            income[1] -> return R.drawable.rent
            income[2] -> return R.drawable.investment
            income[3] -> return R.drawable.selling
            income[4] -> return R.drawable.gift
            income[5] -> return R.drawable.more
            expense[0] -> return R.drawable.bills
            expense[1] -> return R.drawable.grocery
            expense[2] -> return R.drawable.transportation
            expense[3] -> return R.drawable.home
            expense[4] -> return R.drawable.health
            expense[5] -> return R.drawable.car
            expense[6] -> return R.drawable.more
        }
        return R.drawable.more
    }

    override fun formatMoney(num: Double): String {
        val suffix = arrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val value =log10(abs(num))
        val base = (value / 3).toInt()
        return if (value >= 4 && base < suffix.size){
            DecimalFormat("#0.00").format(num / 10.0.pow((base * 3).toDouble())) + suffix[base] + getCurrency()
        } else {
            DecimalFormat("#,##0.00").format(num) + getCurrency()
        }
    }

    override fun checkCompletedGoal() {
        val allGoal = getAllGoal("all")
        for (g in allGoal){
            val amountFilter = if (g.sum > 0.0){
                "positive"
            }else{
                "negative"
            }
            if (abs(getUserBalanceCategory(amountFilter,g.category,g.date)) >= abs(g.sum)){
                setCompletedGoal(true,g.id)
            }else{
                setCompletedGoal(false,g.id)
                setNotified(true,g.id)
            }
        }
    }

    override fun updateBadge() {
        val allGoal = getAllGoal("all")
        var badgeNum = 0
        for (g in allGoal){
            if (g.completed and g.toNotify)
                badgeNum++
        }
        if (badgeNum > 0) {
            goalBadge.number = badgeNum
            goalBadge.isVisible = true
        }else{
            goalBadge.clearNumber()
            goalBadge.isVisible = false
        }
    }
}