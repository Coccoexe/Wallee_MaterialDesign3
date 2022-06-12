package com.example.md3.fragment.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.md3.R
import com.example.md3.utility.IActivityData
import com.google.android.material.textfield.TextInputLayout

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
        val newU : TextInputLayout = inflateView.findViewById(R.id.newUser)
        val newUC : TextInputLayout = inflateView.findViewById(R.id.newUserConfirm)
        val confirm : Button = inflateView.findViewById(R.id.popupOk)
        confirm.setOnClickListener{
            newU.error = null
            newUC.error = null
            //check if new user is empty
            if(newU.editText!!.text.toString() != "") {
                //check if new user is correct
                if (newU.editText!!.text.toString() == newUC.editText!!.text.toString()) {
                    activityData.updateUser(newU.editText!!.text.toString())
                    activityData.removeAutoLog()
                    val textUser : TextView? = activity?.findViewById(R.id.profileName)
                    textUser?.text = newU.editText!!.text.toString()
                    dismiss()
                } else {
                    newUC.error = resources.getString(R.string.username_equals)
                }
            }
            else{
                newU.error = resources.getString(R.string.fill_this_field)
            }
        }

        return inflateView
    }
}