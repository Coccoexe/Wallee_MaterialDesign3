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
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.md3.R
import com.example.md3.utility.IActivityData
import java.lang.RuntimeException

class ChangePassword : DialogFragment() {

    private lateinit var activityData : IActivityData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //inflater
        val inflateView =  inflater.inflate(R.layout.passw_popup, container, false)

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
        val oldP : EditText = inflateView.findViewById(R.id.oldPass)
        val newP : EditText = inflateView.findViewById(R.id.newPass)
        val newPC : EditText = inflateView.findViewById(R.id.newPassConfirm)
        val confirm : Button = inflateView.findViewById(R.id.popupOk)
        confirm.setOnClickListener{

            //check old password
            if (oldP.text.toString() == activityData.getPassword()){
                //check if new password is empty
                if(newP.text.toString() != "") {
                    //check if new password is correct
                    if (newP.text.toString() == newPC.text.toString()) {
                        activityData.updatePassword(newP.text.toString())
                        activityData.removeAutoLog()
                        dismiss()
                    } else {
                        Toast.makeText(context, "New Passwords must be equals!", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(context,"New password cannot be empty!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context,"Old password is incorrect!", Toast.LENGTH_SHORT).show()
            }
        }

        return inflateView
    }
}