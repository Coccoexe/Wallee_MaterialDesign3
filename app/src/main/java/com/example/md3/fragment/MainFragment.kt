package com.example.md3.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.md3.R
import com.example.md3.data.entity.Transaction
import com.example.md3.events.IActivityData
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs


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
        //balance.text = String.format("%.2f",activityData.getUserBalance()) + "$"
        balance.text = activityData.formatMoney(activityData.getUserBalance())

        //add transaction
        val addTrans : FloatingActionButton = inflateView.findViewById(R.id.addTransaction)
        addTrans.setOnClickListener{
            val popup = AddTransPopup()
            popup.show(requireActivity().supportFragmentManager, "popupTransaction")
        }

        //last transaction
        val lastAmount : TextView = inflateView.findViewById(R.id.lastAmount)
        val lastDate : TextView = inflateView.findViewById(R.id.lastDate)
        val lastImage : ImageView = inflateView.findViewById(R.id.lastImage)
        val trans : List<Transaction>? = activityData.getUserWithTransaction()
        if (trans!!.isNotEmpty()) {
            //lastAmount.text = String.format("%.2f",trans.last().amount) + "$ "
            lastAmount.text = activityData.formatMoney(trans.last().amount)
            lastDate.text = trans.last().date
            lastImage.setImageResource(activityData.getDrawable(trans.last().category))
        }else{
            lastDate.text = resources.getString(R.string.no_transaction_found)
        }

        //balanceChart
        val pieChart : PieChart = inflateView.findViewById(R.id.incomeChart)

        //color
        val colors : ArrayList<Int> = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS){
            colors.add(color)
        }

        //take data
        val balanceGraph : ArrayList<PieEntry> = ArrayList()
        var income = activityData.getUserPositiveTransactions()
        if (income == null) {
            income = 0.0
        }
        var expense = activityData.getUserNegativeTransactions()
        expense = if (expense == null) {
            0.0
        }else{
            abs(expense)
        }
        balanceGraph.add(PieEntry(income.toFloat(),"Income"))
        balanceGraph.add(PieEntry(expense.toFloat(),"Expense"))


        val pieDataSet : PieDataSet = PieDataSet(balanceGraph,"Balance")
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 16f

        val pieData : PieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.centerText = "Balance"
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animate()

        pieChart.setEntryLabelTextSize(12f)
        pieChart.holeRadius = 80f
        pieChart.setDrawEntryLabels(false)
        pieChart.contentDescription = ""

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {

            }

            override fun onNothingSelected() {}
        })

        // Inflate the layout for this fragment
        return inflateView
    }

}