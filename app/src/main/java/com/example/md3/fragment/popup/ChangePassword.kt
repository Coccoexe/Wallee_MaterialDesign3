package com.example.md3.fragment.popup


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.md3.R
import com.example.md3.utility.IActivityData
import com.google.android.material.textfield.TextInputLayout
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
        val oldP : TextInputLayout = inflateView.findViewById(R.id.oldPass)
        val newP : TextInputLayout = inflateView.findViewById(R.id.newPass)
        val newPC : TextInputLayout = inflateView.findViewById(R.id.newPassConfirm)
        val confirm : Button = inflateView.findViewById(R.id.popupOk)
        confirm.setOnClickListener{
            oldP.error = null
            newP.error = null
            newPC.error = null
            //check old password
            if (oldP.editText!!.text.toString() == activityData.getPassword()){
                //check if new password is empty
                if(newP.editText!!.text.toString() != "") {
                    //check if new password is correct
                    if (newP.editText!!.text.toString() == newPC.editText!!.text.toString()) {
                        activityData.updatePassword(newP.editText!!.text.toString())
                        activityData.removeAutoLog()
                        dismiss()
                    } else {
                        newPC.error = resources.getString(R.string.password_equals)
                    }
                }
                else{
                    newP.error = resources.getString(R.string.fill_this_field)
                }
            }else{
                oldP.error = resources.getString(R.string.old_password_wrong)
            }
        }

        return inflateView
    }
}