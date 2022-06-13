package com.example.md3.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.view.ActionMode
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.activity.MainActivity
import com.example.md3.R
import com.example.md3.adapter.CardGoalAdapter
import com.example.md3.data.entity.Goal
import com.example.md3.fragment.popup.AddGoalPopup
import com.example.md3.utility.IActivityData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GoalFragment : Fragment() {

    private lateinit var activityData : IActivityData
    private var goalList : List<Goal>? = null
    private lateinit var gridLayoutManager: GridLayoutManager
    private var filterToggle : Boolean = false
    private var color: Int = -1

    //view
    private lateinit var topAppBar : MaterialToolbar
    private lateinit var filterBar : ConstraintLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var noGoalText : TextView
    private lateinit var filterAmountText : TextView
    private lateinit var transactionGroup : MaterialButtonToggleGroup
    private lateinit var all : MaterialButton
    private lateinit var positive : MaterialButton
    private lateinit var negative : MaterialButton
    private lateinit var cardFAB : FloatingActionButton

    //contextBar
    private var actionMode: ActionMode? = null

    //amount -> "all", "positive", "negative"
    private lateinit var filterAmount : String

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

        topAppBar = inflateView.findViewById(R.id.topAppBarGoal)
        filterBar = inflateView.findViewById(R.id.filterBarGoal)
        filterAmountText = inflateView.findViewById(R.id.transactionFilterTextGoal)
        cardFAB = inflateView.findViewById(R.id.cardFAB)
        gridLayoutManager = GridLayoutManager(context,1, GridLayoutManager.VERTICAL,false)
        //amount
        transactionGroup = inflateView.findViewById(R.id.selectTransactionGoal)
        all = inflateView.findViewById(R.id.allTransactionGoal)
        positive = inflateView.findViewById(R.id.positiveTransactionGoal)
        negative = inflateView.findViewById(R.id.negativeTransactionGoal)
        color = MaterialColors.getColor(inflateView,com.google.android.material.R.attr.colorOnSecondaryContainer)

        //default

        //filterBar
        filterBar.visibility = View.GONE
        //amount
        filterAmount = "all"
        filterAmountText.visibility = View.GONE
        transactionGroup.visibility = View.GONE
        transactionGroup.check(R.id.allTransactionGoal)
        all.iconTint = null
        all.setIconResource(R.drawable.money_in_out_color)

        //recyclerview
        recyclerView = inflateView.findViewById(R.id.recyclerViewCard)
        //no view available
        noGoalText = inflateView.findViewById(R.id.noGoal)
        //gridLayout
        recyclerView.layoutManager = gridLayoutManager
        //adapter
        getGoalList()
        setAdapter()

        //updateBadgeStatus
        checkToNotify()
        activityData.updateBadge()

        //listener
        cardFAB.setOnClickListener {
            val popup = AddGoalPopup()
            popup.show(childFragmentManager, "popupGoal")
        }

        //apre e chiude il menu dei filtri
        topAppBar.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId){
                R.id.filterMenu -> {
                    if (filterToggle)
                    {
                        topAppBar.menu.getItem(0).setIcon(R.drawable.ic_filter_down_24)
                        filterBar.visibility = View.GONE
                        filterAmountText.visibility = View.GONE
                        transactionGroup.visibility = View.GONE

                        filterToggle = false
                    }
                    else{
                        topAppBar.menu.getItem(0).setIcon(R.drawable.ic_filter_up_24)
                        filterBar.visibility = View.VISIBLE
                        filterAmountText.visibility = View.VISIBLE
                        transactionGroup.visibility = View.VISIBLE

                        filterToggle = true
                    }
                    true
                }
                else -> false
            }
        }

        //applica il filtro tipo di transazione(tutte,positive,negative)
        transactionGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when(checkedId) {
                    R.id.allTransactionGoal -> {
                        filterAmount = "all"

                        all.iconTint = null
                        all.setIconResource(R.drawable.money_in_out_color)

                        positive.iconTint = ColorStateList.valueOf(color)
                        positive.setIconResource(R.drawable.money_in)

                        negative.iconTint = ColorStateList.valueOf(color)
                        negative.setIconResource(R.drawable.money_out)
                    }
                    R.id.positiveTransactionGoal -> {
                        filterAmount = "positive"

                        all.iconTint = ColorStateList.valueOf(color)
                        all.setIconResource(R.drawable.money_in_out)

                        positive.iconTint = null
                        positive.setIconResource(R.drawable.money_in_color)

                        negative.iconTint = ColorStateList.valueOf(color)
                        negative.setIconResource(R.drawable.money_out)
                    }
                    R.id.negativeTransactionGoal -> {
                        filterAmount = "negative"

                        all.iconTint = ColorStateList.valueOf(color)
                        all.setIconResource(R.drawable.money_in_out)

                        positive.iconTint = ColorStateList.valueOf(color)
                        positive.setIconResource(R.drawable.money_in)

                        negative.iconTint = null
                        negative.setIconResource(R.drawable.money_out_color)
                    }
                }
            }

            getGoalList()
            setAdapter()
        }

        // Inflate the layout for this fragment
        return inflateView
    }

    fun openContextBar(){
        actionMode = (activity as MainActivity?)!!.startSupportActionMode(mActionModeCallback)

        if (filterToggle) {
            topAppBar.menu.getItem(0).setIcon(R.drawable.ic_filter_down_24)
            filterBar.visibility = View.GONE
            filterAmountText.visibility = View.GONE
            transactionGroup.visibility = View.GONE
            filterToggle = false
        }
    }

    fun closeContextBar() {
        actionMode?.finish()
    }

    fun updateContextBarTitle(n : Int){
        actionMode?.title = resources.getString(R.string.n_item_selected).format(n)
        if (n == goalList!!.size) {
            actionMode?.menu?.getItem(0)?.setIcon(R.drawable.ic_check_all_24)
        } else {
            actionMode?.menu?.getItem(0)?.setIcon(R.drawable.ic_check_24)
        }
    }

    fun getFormattedMoney(num : Double) : String{
        return activityData.formatMoney(num)
    }

    fun getBalanceGoal(filterAmount : String, category: String, date: String) : Double {
        return activityData.getUserBalanceCategory(filterAmount, category, date)
    }

    //action bar
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
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(resources.getString(R.string.delete_selected))
                        .setMessage(resources.getString(R.string.n_selected_deleted).format((recyclerView.adapter as CardGoalAdapter).selected.size))
                        .setNeutralButton(resources.getString(R.string.cancel)){ _, _ ->
                            mode.finish()
                        }
                        .setPositiveButton(resources.getString(R.string.accept)){ _, _ ->
                            activityData.removeSelectedGoal((recyclerView.adapter as CardGoalAdapter).selected)
                            mode.finish()
                        }
                        .show()
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
        goalList = activityData.getAllGoal(filterAmount)
    }

    fun setAdapter(){
        if (!goalList.isNullOrEmpty()) {
            noGoalText.visibility = View.INVISIBLE
            recyclerView.adapter = CardGoalAdapter(context, goalList!!, this)
        }
        else{
            noGoalText.visibility = View.VISIBLE
            recyclerView.adapter = null
        }
    }

    private fun checkToNotify(){
        val allGoal = activityData.getAllGoal("all")
        for (g in allGoal!!){
            if (g.completed and g.toNotify){
                activityData.setNotified(false,g.id)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        actionMode?.finish()
    }
}