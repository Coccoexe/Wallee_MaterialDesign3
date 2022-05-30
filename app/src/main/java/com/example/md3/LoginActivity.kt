package com.example.md3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.md3.data.UserDatabase
import com.example.md3.data.entity.AutoLogin
import com.example.md3.data.entity.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {

    private lateinit var userMail : EditText
    private lateinit var userPass : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //database
        val dao = UserDatabase.getInstance(this).userDao

        //button
        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton : Button = findViewById(R.id.registerButton)
        userMail = findViewById(R.id.emailText)
        userPass = findViewById(R.id.passText)

        //clear edit text
        userMail.text.clear()
        userPass.text.clear()

        //autologin
        val checkLog : CheckBox = findViewById(R.id.autoLog)
        runBlocking {
            val autoLogin: AutoLogin? = dao.getAutoLogin()
            if(autoLogin != null)
            {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra("email",autoLogin.userMail)
                startActivity(intent)
            }
        }

        loginButton.setOnClickListener {
            val mail = userMail.text.toString()
            val pass = userPass.text.toString()
            if (mail.isEmpty() || pass.isEmpty()){
                Toast.makeText(applicationContext,"Fill all fields!", Toast.LENGTH_SHORT).show()
            }
            else{

                lifecycleScope.launch{
                    val user: User? = dao.login(mail,pass)
                    if (user == null){
                        Toast.makeText(applicationContext,"Invalid Credentials!", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        dao.removeAutoLog()

                        if(checkLog.isChecked)
                        {
                            dao.insertAutoLog(AutoLogin(mail, pass))
                        }

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.putExtra("email",mail)
                        startActivity(intent)
                    }
                }
            }
        }

        registerButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        userMail.text.clear()
        userPass.text.clear()
    }
}