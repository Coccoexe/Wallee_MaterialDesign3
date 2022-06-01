package com.example.md3

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.md3.data.UserDao
import com.example.md3.data.UserDatabase
import com.example.md3.data.entity.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton : Button = findViewById(R.id.registerButton)
        val userMail : EditText = findViewById(R.id.emailText)
        val userPass : EditText = findViewById(R.id.passText)
        val userName : EditText = findViewById(R.id.nameText)
        val userUri : Uri = Uri.parse("android.resource://com.example.md3/drawable/no_image")
        val dao = UserDatabase.getInstance(this).userDao

        registerButton.setOnClickListener {

            val user = User(
                0,
                userMail.text.toString(),
                userName.text.toString(),
                userPass.text.toString(),
                userUri.toString()
            )
            if (existMail(dao,user)) {
                Toast.makeText(applicationContext, "Email already used!", Toast.LENGTH_SHORT)
                    .show()
                userMail.text.clear()
            } else {
                if (validateInput(user)) {
                    runBlocking {
                        dao.insertUser(user)
                    }
                    Toast.makeText(applicationContext, "User Registered!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Fill all fields!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun validateInput(user: User): Boolean {
        return !(user.userMail.isEmpty() || user.userName.isEmpty() || user.password.isEmpty())
    }

    private fun existMail(dao: UserDao, user: User): Boolean {
        var exist : Int? = null
        runBlocking {
            exist = dao.getId(user.userMail)
        }
        return exist != null
    }

}
