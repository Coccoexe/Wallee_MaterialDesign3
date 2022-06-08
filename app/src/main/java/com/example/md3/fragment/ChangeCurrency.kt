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
import com.example.md3.utility.IActivityData
import com.google.android.material.textfield.TextInputLayout

class ChangeCurrency : DialogFragment() {

    private lateinit var activityData : IActivityData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //inflater
        val inflateView =  inflater.inflate(R.layout.currency_popup, container, false)

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        //cancelButton
        val cancel : Button = inflateView.findViewById(R.id.popupBack)
        cancel.setOnClickListener{
            dismiss()
        }

        //menu tendina
        val categoryCurrency: TextInputLayout = inflateView.findViewById(R.id.selectCurrency)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, resources.getStringArray(R.array.currency))
        (categoryCurrency.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        //confirmButton
        val confirm : Button = inflateView.findViewById(R.id.popupOk)
        confirm.setOnClickListener{
            if(categoryCurrency.editText!!.text.isNotEmpty()){
                activityData.updateCurrency(categoryCurrency.editText!!.text.toString())
            }
            dismiss()
        }

        return inflateView
    }

}