package com.example.md3.fragment

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.MainActivity
import com.example.md3.R
import com.example.md3.adapter.CardGoalAdapter
import com.example.md3.adapter.TransactionAdapter
import com.example.md3.data.entity.Goal
import com.example.md3.events.IActivityData
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GoalFragment : Fragment() {

    private lateinit var activityData : IActivityData
    private var goalList : List<Goal>? = null

    private lateinit var cardFAB : FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var noGoalText : TextView

    //contextBar
    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflateView = inflater.inflate(R.layout.fragment_goal, container, false)
        DynamicColors.applyToActivityIfAvailable(requireActivity())

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData


        cardFAB = inflateView.findViewById(R.id.cardFAB)
        recyclerView = inflateView.findViewById(R.id.recyclerViewCard)
        noGoalText = inflateView.findViewById(R.id.noGoal)
        gridLayoutManager = GridLayoutManager(context,1, GridLayoutManager.VERTICAL,false)


        //default
        recyclerView.layoutManager = gridLayoutManager
        getGoalList()
        setAdapter()


        //listener
        cardFAB.setOnClickListener {
            val popup = AddGoalPopup()
            popup.show(childFragmentManager, "popupGoal")
        }

        // Inflate the layout for this fragment
        return inflateView
    }

    fun openContextBar(){
        actionMode = (activity as MainActivity?)!!.startSupportActionMode(mActionModeCallback)
    }

    fun closeContextBar() {
        actionMode?.finish()
    }

    fun updateContextBarTitle(n : Int){
        actionMode?.title = "$n Item Selected"
        if (n == goalList!!.size) {
            actionMode?.menu?.getItem(0)?.setIcon(R.drawable.ic_check_all_24)
        } else {
            actionMode?.menu?.getItem(0)?.setIcon(R.drawable.ic_check_24)
        }
    }

    fun getFormattedMoney(num : Double) : String{
        return activityData.formatMoney(num)
    }

    fun getBalanceGoal(category: String) : Double {
        return activityData.getUserBalanceCategory(category)
    }

    private val mActionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
            mode.menuInflater.inflate(R.menu.context_transaction_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.selectAll -> {
                    (recyclerView.adapter as CardGoalAdapter).selectionAll()
                    true
                }
                R.id.deleteSelected -> {
                    activityData.removeSelectedGoal((recyclerView.adapter as CardGoalAdapter).selected)
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            (recyclerView.adapter as CardGoalAdapter).selected.clear()
            (recyclerView.adapter as CardGoalAdapter).selectionMode = false
            getGoalList()
            setAdapter()
        }
    }

    fun getGoalList(){
        goalList = activityData.getAllGoal()
    }

    fun setAdapter(){
        if (!goalList.isNullOrEmpty()) {
            noGoalText.visibility = View.INVISIBLE
            recyclerView.adapter = CardGoalAdapter(context,goalList!!, this)
        }
        else{
            noGoalText.visibility = View.VISIBLE
            recyclerView.adapter = null
        }
    }
}