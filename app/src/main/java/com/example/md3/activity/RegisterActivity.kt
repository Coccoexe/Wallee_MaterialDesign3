package com.example.md3.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.example.md3.R
import com.example.md3.data.UserDao
import com.example.md3.data.UserDatabase
import com.example.md3.data.entity.User
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.runBlocking

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton : Button = findViewById(R.id.registerButton)
        val userMail : TextInputLayout = findViewById(R.id.emailText)
        val userPass : TextInputLayout = findViewById(R.id.passText)
        val userName : TextInputLayout = findViewById(R.id.nameText)
        val backButton : AppCompatImageView = findViewById(R.id.registerBack)
        val dao = UserDatabase.getInstance(this).userDao

        backButton.setOnClickListener{
            finish()
        }

        registerButton.setOnClickListener {

            userMail.error = null
            userPass.error = null
            userName.error = null

            val user = User(
                0,
                userMail.editText!!.text.toString(),
                userName.editText!!.text.toString(),
                userPass.editText!!.text.toString(),
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.no_image),
                resources.getStringArray(R.array.currency)[0]
            )
            if(!validateInput(user))
            {
                if (userMail.editText!!.text.isEmpty())
                {
                    userMail.error = resources.getString(R.string.fill_this_field)
                }
                if (userPass.editText!!.text.isEmpty())
                {
                    userPass.error = resources.getString(R.string.fill_this_field)
                }
                if (userName.editText!!.text.isEmpty())
                {
                    userName.error = resources.getString(R.string.fill_this_field)
                }
            }else{
                if (existMail(dao,user)) {
                    userMail.error = resources.getString(R.string.email_already_used)
                    userMail.editText!!.text.clear()
                }else{
                    runBlocking {
                        dao.insertUser(user)
                    }
                    Toast.makeText(applicationContext, resources.getString(R.string.user_registered), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun validateInput(user: User): Boolean {
        return !(user.userMail.isEmpty() || user.userName.isEmpty() || user.password.isEmpty())
    }

    private fun existMail(dao: UserDao, user: User): Boolean {
        var exist: Int?
        runBlocking {
            exist = dao.getId(user.userMail)
        }
        return exist != null
    }

}
