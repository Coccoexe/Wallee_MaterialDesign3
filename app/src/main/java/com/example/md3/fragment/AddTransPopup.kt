package com.example.md3.fragment


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.fragment.app.DialogFragment
import com.example.md3.R


class addTransPopup : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //inflater
        val inflateView =  inflater.inflate(R.layout.trans_popup, container, false)

        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        //menu tendina
        val transCat : Spinner = inflateView.findViewById(R.id.popupSpinner)
        var items = arrayOf<String>("Categoria1","Categoria2","Altro")
        val adapter: Any =
            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        transCat.adapter = adapter as SpinnerAdapter?

        //cancelButton
        val cancel : Button = inflateView.findViewById(R.id.popupBack)
        cancel.setOnClickListener{
            dismiss()
        }


        return inflateView
    }
}