package com.example.md3.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.md3.R
import com.example.md3.events.IActivityData
import java.lang.RuntimeException

class ProfileFragment : Fragment() {

    private lateinit var activityData : IActivityData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflaterView = inflater.inflate(R.layout.fragment_profile, container, false)

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        val backButton : AppCompatImageView = inflaterView.findViewById(R.id.profileBack)
        backButton.setOnClickListener{
            val controller : NavController = requireActivity().findNavController(R.id.navigationController)
            if(controller.backQueue.size > 2)
            {
                controller.popBackStack()
            }
        }

        val cardLogout : CardView = inflaterView.findViewById(R.id.cardLogout)
        cardLogout.setOnClickListener{
            activityData.removeAutoLog()
            requireActivity().finish()
        }

        return inflaterView
    }

}