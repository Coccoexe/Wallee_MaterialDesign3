package com.example.md3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inflateView = inflater.inflate(R.layout.fragment_main, container, false)

        DynamicColors.applyToActivityIfAvailable(requireActivity())

        val addTrans : Button = inflateView.findViewById(R.id.addTransaction)
        addTrans.setOnClickListener{
            val popup = addTransPopup()

            popup.show(requireActivity().supportFragmentManager, "popupTransaction")
        }

        val profileButton : FloatingActionButton = inflateView.findViewById(R.id.userPicture)
        profileButton.setOnClickListener{
            inflateView.findNavController().navigate(R.id.action_profile)
        }

        // Inflate the layout for this fragment
        return inflateView
    }

}