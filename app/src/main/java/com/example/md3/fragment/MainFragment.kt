package com.example.md3.fragment

import android.R.attr.bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.md3.R
import com.example.md3.data.entity.Transaction
import com.example.md3.events.IActivityData
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainFragment : Fragment() {

    private lateinit var activityData : IActivityData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inflateView = inflater.inflate(R.layout.fragment_main, container, false)
        DynamicColors.applyToActivityIfAvailable(requireActivity())

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        //welcome
        val welcome : TextView = inflateView.findViewById(R.id.welcome)
        welcome.text = getString(R.string.user_welcome).format(activityData.getUserName())

        //balance
        val balance : TextView = inflateView.findViewById(R.id.balance)
        balance.text = activityData.getUserBalance().toString() + "$"

        //add transaction
        val addTrans : Button = inflateView.findViewById(R.id.addTransaction)
        addTrans.setOnClickListener{
            val popup = AddTransPopup()
            popup.show(requireActivity().supportFragmentManager, "popupTransaction")
        }

        //profile button
        val profileButton : FloatingActionButton = inflateView.findViewById(R.id.userPicture)
        profileButton.setOnClickListener{
            inflateView.findNavController().navigate(R.id.action_profile)
        }

        //last transaction
        val lastAmount : TextView = inflateView.findViewById(R.id.lastAmount)
        val lastDate : TextView = inflateView.findViewById(R.id.lastDate)
        val lastImage : ImageView = inflateView.findViewById(R.id.lastImage)
        val trans : List<Transaction>? = activityData.getUserWithTransaction()
        if (trans!!.isNotEmpty()) {
            lastAmount.text = trans.last().amount.toString() + "$ "
            lastDate.text = trans.last().date
            lastImage.setImageResource(getDrawable(trans.last().category))
        }else{
            lastAmount.text = "0 $"
            lastDate.text = "No recent transaction"
            lastImage.setImageResource(R.drawable.more)
        }


        // Inflate the layout for this fragment
        return inflateView
    }

    private fun getDrawable(category : String) : Int{
        val income = resources.getStringArray(R.array.income)
        val expense = resources.getStringArray(R.array.expenses)
        when(category){
            income[0] -> return R.drawable.salary
            income[1] -> return R.drawable.rent
            income[2] -> return R.drawable.investment
            income[3] -> return R.drawable.selling
            income[4] -> return R.drawable.gift
            income[5] -> return R.drawable.more
            expense[0] -> return R.drawable.bills
            expense[1] -> return R.drawable.grocery
            expense[2] -> return R.drawable.transportation
            expense[3] -> return R.drawable.home
            expense[4] -> return R.drawable.health
            expense[5] -> return R.drawable.gift
            expense[6] -> return R.drawable.more
        }
        return R.drawable.more
    }

}