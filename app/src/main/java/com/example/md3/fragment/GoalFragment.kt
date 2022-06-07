package com.example.md3.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.adapter.CardGoalAdapter
import com.example.md3.data.entity.Goal
import com.example.md3.events.IActivityData
import com.google.android.material.floatingactionbutton.FloatingActionButton


class GoalFragment : Fragment() {

    private lateinit var activityData : IActivityData
    private var goalList : List<Goal>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        val inflateView = inflater.inflate(R.layout.fragment_goal, container, false)

        val cardFAB : FloatingActionButton= inflateView.findViewById(R.id.cardFAB)

        cardFAB.setOnClickListener {
            val popup = AddGoalPopup()
            popup.show(requireActivity().supportFragmentManager, "popupGoal")
        }

        val recyclerView: RecyclerView = inflateView.findViewById(R.id.recyclerViewCard)
        val layoutManager = GridLayoutManager(context,1, GridLayoutManager.VERTICAL,false)
        recyclerView.layoutManager = layoutManager

        goalList = activityData.getAllGoal()
        recyclerView.adapter = CardGoalAdapter(goalList!!, this)

        // Inflate the layout for this fragment
        return inflateView
    }

    fun getBalanceGoal(category: String) : Double {
        return activityData.getUserBalanceCategory(category)
    }
}