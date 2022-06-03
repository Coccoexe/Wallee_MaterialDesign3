package com.example.md3.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.md3.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class GoalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inflateView = inflater.inflate(R.layout.fragment_goal, container, false)

        val cardFAB : FloatingActionButton= inflateView.findViewById(R.id.cardFAB)

        cardFAB.setOnClickListener {
            Toast.makeText(this.context, "FAB is clicked", Toast.LENGTH_SHORT).show()
            //val popup = AddCardPopup()
            //popup.show(requireActivity().supportFragmentManager, "addCard")
        }

        // Inflate the layout for this fragment
        return inflateView
    }
}