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
    private lateinit var loginButton: Button
    private lateinit var registerButton : Button
    private lateinit var checkLog : CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //database
        val dao = UserDatabase.getInstance(this).userDao

        //button
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        userMail = findViewById(R.id.emailText)
        userPass = findViewById(R.id.passText)
        checkLog = findViewById(R.id.autoLog)

        //clear edit text
        userMail.editText?.text?.clear()
        userPass.editText?.text?.clear()

        //autologin
        // se in precedenza l'utente ha spuntato l'autologin, viene automaticamente
        // effettuato l'accesso all'app
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

            //controllo la correttezza dei dati inseriti e accedo
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

                        //rimuovo l'autologin nel caso ci sia
                        dao.removeAutoLog()

                        //se l'utente ha deciso di salvare il login
                        //aggiorno il database per accedere automaticamente la
                        //volta successiva
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

    // voglio sempre i campi email e password puliti quando viene ricreato il fragment
    // sia in apertura sia dopo la registrazione
    override fun onResume() {
        super.onResume()
        userMail.editText!!.text.clear()
        userPass.editText!!.text.clear()
        userMail.error = null
        userPass.error = null
    }
}