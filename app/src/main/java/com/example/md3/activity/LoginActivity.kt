package com.example.md3.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.example.md3.R
import com.example.md3.data.UserDatabase
import com.example.md3.data.entity.AutoLogin
import com.example.md3.data.entity.User
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {

    private lateinit var userMail : TextInputLayout
    private lateinit var userPass : TextInputLayout

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
        userMail.editText?.text?.clear()
        userPass.editText?.text?.clear()

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
            val mail = userMail.editText!!.text.toString()
            val pass = userPass.editText!!.text.toString()

            userMail.error = null
            userPass.error = null

            if (mail.isEmpty() || pass.isEmpty()){
                if(mail.isEmpty()) {
                    userMail.error = resources.getString(R.string.fill_this_field)
                }
                if(pass.isEmpty()){
                    userPass.error = resources.getString(R.string.fill_this_field)
                }
            }
            else{
                runBlocking{
                    val user: User? = dao.login(mail,pass)
                    if (user == null){
                        userMail.error = resources.getString(R.string.invalid_mail)
                        userPass.error = resources.getString(R.string.invalid_password)
                    }
                    else
                    {
                        userMail.error = null
                        userPass.error = null

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
        userMail.editText!!.text.clear()
        userPass.editText!!.text.clear()
        userMail.error = null
        userPass.error = null
    }
}