package com.example.md3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.md3.data.UserDatabase
import com.example.md3.data.entity.User
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton : Button = findViewById(R.id.registerButton)
        val userMail : EditText = findViewById(R.id.emailText)
        val userPass : EditText = findViewById(R.id.passText)


        loginButton.setOnClickListener {
            val mail = userMail.text.toString()
            val pass = userPass.text.toString()
            if (mail.isEmpty() || pass.isEmpty()){
                Toast.makeText(applicationContext,"Fill all fields!", Toast.LENGTH_SHORT).show()
            }
            else{
                val dao = UserDatabase.getInstance(this).userDao
                lifecycleScope.launch{
                    val user: User? = dao.login(mail,pass)
                    if (user == null){
                        Toast.makeText(applicationContext,"Invalid Credentials!", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
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
}