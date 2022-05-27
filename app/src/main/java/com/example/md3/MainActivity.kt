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

            /*
            val transCat : Spinner = findViewById(R.id.popupSpinner)

            var items = arrayOf<String>("Categoria1","Categoria2","Altro")

            val adapter: Any =
                ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_dropdown_item, items)

            transCat.adapter = adapter as SpinnerAdapter?
            */
        }
    }
}