package com.example.md3.fragment.popup

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.md3.R
import com.example.md3.utility.IActivityData
import com.google.android.material.textfield.TextInputLayout


class ChangeEmail : DialogFragment() {

    private lateinit var activityData : IActivityData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //inflater
        val inflateView =  inflater.inflate(R.layout.mail_popup, container, false)

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

        //confirmButton
        val newM : TextInputLayout = inflateView.findViewById(R.id.newMail)
        val newMC : TextInputLayout = inflateView.findViewById(R.id.newMailConfirm)
        val confirm : Button = inflateView.findViewById(R.id.popupOk)
        confirm.setOnClickListener{
            newM.error = null
            newMC.error = null
            //check if new mail is empty
            if(newM.editText!!.text.toString() != "") {
                //check if new mail is correct
                if (activityData.existMail(newM.editText!!.text.toString())){
                    newM.error = "Email already used!"
                    newM.editText!!.text.clear()
                    newMC.editText!!.text.clear()
                } else {
                    if (newM.editText!!.text.toString() == newMC.editText!!.text.toString()) {
                        activityData.updateEmail(newM.editText!!.text.toString())
                        activityData.removeAutoLog()
                        val textEmail: TextView? = activity?.findViewById(R.id.emailProfile)
                        textEmail?.text = newM.editText!!.text.toString()
                        dismiss()
                    } else {
                        newMC.editText!!.text.clear()
                        newMC.error = "Mail Confirm must be same as New Mail"
                    }
                }
            } else {
                newM.error = "Fill this field!"
            }
        }

        return inflateView
    }

}