package com.example.md3.fragment

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
        val newM : EditText = inflateView.findViewById(R.id.newMail)
        val newMC : EditText = inflateView.findViewById(R.id.newMailConfirm)
        val confirm : Button = inflateView.findViewById(R.id.popupOk)
        confirm.setOnClickListener{

            //check if new mail is empty
            if(newM.text.toString() != "") {
                //check if new mail is correct
                if (activityData.existMail(newM.text.toString())){
                    Toast.makeText(context, "Email already used!", Toast.LENGTH_SHORT)
                        .show()
                    newM.text.clear()
                    newMC.text.clear()
                } else {
                    if (newM.text.toString() == newMC.text.toString()) {
                        activityData.updateEmail(newM.text.toString())
                        activityData.removeAutoLog()
                        val textEmail: TextView? = activity?.findViewById(R.id.emailProfile)
                        textEmail?.text = newM.text.toString()
                        dismiss()
                    } else {
                        Toast.makeText(context, "New Mail must be equals!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(context,"New Mail cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        return inflateView
    }

}