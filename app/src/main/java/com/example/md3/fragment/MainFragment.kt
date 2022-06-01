package com.example.md3.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.md3.R
import com.example.md3.data.entity.Transaction
import com.example.md3.events.IActivityData
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.RuntimeException


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
        val lastTransaction : TextView = inflateView.findViewById(R.id.last_transactions)
        val trans : List<Transaction>? = activityData.getUserWithTransaction()
        if (trans!!.isNotEmpty()) {
            lastTransaction.text = trans[0].amount.toString() + " " + trans[0].date
        }

        // Inflate the layout for this fragment
        return inflateView
    }

}