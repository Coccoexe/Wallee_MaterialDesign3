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

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflaterView = inflater.inflate(R.layout.fragment_profile, container, false)

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
            requireActivity().finish()
        }

        return inflaterView
    }

}