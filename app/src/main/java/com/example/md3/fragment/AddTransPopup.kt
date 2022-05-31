package com.example.md3.fragment


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.md3.R


class AddTransPopup : DialogFragment() {

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

        //radiogroup
        val radiogroup : RadioGroup = inflateView.findViewById(R.id.selectTransaction)
        radiogroup.setOnCheckedChangeListener { _, optionId ->
            run {
                when (optionId) {
                    R.id.add -> {
                        val items = resources.getStringArray(R.array.income)
                        val adapter: Any =
                            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
                        transCat.adapter = adapter as SpinnerAdapter?
                    }
                    R.id.remove -> {
                        val items = resources.getStringArray(R.array.expenses)
                        val adapter: Any =
                            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
                        transCat.adapter = adapter as SpinnerAdapter?
                    }
                }
            }
        }

        //confirmButton
        val confirm : Button = inflateView.findViewById(R.id.popupOk)
        confirm.setOnClickListener{
            dismiss()
        }

        //cancelButton
        val cancel : Button = inflateView.findViewById(R.id.popupBack)
        cancel.setOnClickListener{
            dismiss()
        }


        return inflateView
    }
}