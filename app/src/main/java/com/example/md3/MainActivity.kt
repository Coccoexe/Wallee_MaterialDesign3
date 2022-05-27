package com.example.md3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DynamicColors.applyToActivityIfAvailable(this)

        val addTrans : Button = findViewById(R.id.addTransaction)
        addTrans.setOnClickListener{
            var popup = addTransPopup()

            popup.show(supportFragmentManager, "popupTransaction")


        }
    }
}