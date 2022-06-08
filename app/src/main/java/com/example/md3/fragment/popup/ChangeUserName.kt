package com.example.md3.fragment.popup

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

class ChangeUserName : DialogFragment(){
    private lateinit var activityData : IActivityData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //inflater
        val inflateView =  inflater.inflate(R.layout.user_popup, container, false)

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
        val newU : EditText = inflateView.findViewById(R.id.newUser)
        val newUC : EditText = inflateView.findViewById(R.id.newUserConfirm)
        val confirm : Button = inflateView.findViewById(R.id.popupOk)
        confirm.setOnClickListener{

            //check if new user is empty
            if(newU.text.toString() != "") {
                //check if new user is correct
                if (newU.text.toString() == newUC.text.toString()) {
                    activityData.updateUser(newU.text.toString())
                    activityData.removeAutoLog()
                    val textUser : TextView? = activity?.findViewById(R.id.profileName)
                    textUser?.text = newU.text.toString()
                    dismiss()
                } else {
                    Toast.makeText(context, "New Usernames must be equals!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(context,"New Username cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        return inflateView
    }
}