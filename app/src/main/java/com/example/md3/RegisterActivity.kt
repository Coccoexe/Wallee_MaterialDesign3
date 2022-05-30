package com.example.md3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.md3.data.UserDatabase
import com.example.md3.data.entity.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton : Button = findViewById(R.id.registerButton)
        val userMail : EditText = findViewById(R.id.emailText)
        val userPass : EditText = findViewById(R.id.passText)
        val userName : EditText = findViewById(R.id.nameText)

        registerButton.setOnClickListener{

            val user : User = User(
                userMail.text.toString(),
                userName.text.toString(),
                userPass.text.toString()
            )
            if(validateInput(user)){
                val dao = UserDatabase.getInstance(this).userDao
                lifecycleScope.launch{
                    dao.insertUser(user)
                }
                Toast.makeText(applicationContext,"User Registered!",Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(applicationContext,"Fill all fields!",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun validateInput(user: User): Boolean {
        return !(user.userMail.isEmpty() || user.userName.isEmpty() || user.password.isEmpty())
    }

}
