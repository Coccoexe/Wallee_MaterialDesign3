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
import com.example.md3.utility.IActivityData
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs


class MainFragment : Fragment() {

    private lateinit var activityData : IActivityData

    private lateinit var welcome : TextView
    private lateinit var balance : TextView
    private lateinit var addTrans : FloatingActionButton
    private lateinit var lastAmount : TextView
    private lateinit var lastDate : TextView
    private lateinit var lastImage : ImageView
    private lateinit var pieChart : PieChart
    private lateinit var legend : TextView

    private var trans : List<Transaction>? = null
    private var color : Int = -1
    private var legendEntry : String = ""

    private lateinit var colors : ArrayList<Int>
    private lateinit var balanceGraph : ArrayList<PieEntry>
    private var incomeList: Map<String,Double>? = null
    private var expenseList: Map<String,Double>? = null

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

        //init
        welcome = inflateView.findViewById(R.id.welcome)
        balance = inflateView.findViewById(R.id.balance)
        addTrans= inflateView.findViewById(R.id.addTransaction)
        lastAmount = inflateView.findViewById(R.id.lastAmount)
        lastDate = inflateView.findViewById(R.id.lastDate)
        lastImage  = inflateView.findViewById(R.id.lastImage)
        pieChart = inflateView.findViewById(R.id.balanceChart)
        legend = inflateView.findViewById(R.id.legendChart)

        //default
        welcome.text = getString(R.string.user_welcome).format(activityData.getUserName())
        balance.text = activityData.formatMoney(activityData.getUserBalance())
        color = MaterialColors.getColor(inflateView,com.google.android.material.R.attr.colorOnSecondaryContainer)
        trans = activityData.getUserWithTransaction()
        if (trans!!.isNotEmpty()) {
            //lastAmount.text = String.format("%.2f",trans.last().amount) + "$ "
            lastAmount.text = activityData.formatMoney(trans!!.last().amount)
            lastDate.text = trans!!.last().date
            lastImage.setImageResource(activityData.getDrawable(trans!!.last().category))
        }else{
            lastDate.text = resources.getString(R.string.no_transaction_found)
        }

        //chart
        //color
        colors = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS){
            colors.add(color)
        }
        //initialize chart
        getChartData()

        //listener
        addTrans.setOnClickListener{
            val popup = AddTransPopup()
            //popup.show(requireActivity().supportFragmentManager, "popupTransaction")
            popup.show(childFragmentManager, "popupTransaction")
        }

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                legendEntry = ""
                when((e as PieEntry).label){
                    "Income" -> {
                        if (incomeList != null){
                            for (item in incomeList!!){
                                legendEntry += item.key + " : " + activityData.formatMoney(item.value) + '\n'
                            }
                        }
                    }
                    "Expense" -> {
                        if (expenseList != null){
                            for (item in expenseList!!){
                                legendEntry += item.key + " : " + activityData.formatMoney(item.value) + '\n'
                            }
                        }
                    }
                }
                legend.text = legendEntry
                legend.visibility = View.VISIBLE
            }

            override fun onNothingSelected() {
                legendEntry = ""
                legend.visibility = View.GONE
            }
        })

        // Inflate the layout for this fragment
        return inflateView
    }

    fun updateChart(){
        getChartData()
        pieChart.notifyDataSetChanged()
        pieChart.invalidate()
    }

    private fun getChartData(){
        balanceGraph = ArrayList()
        incomeList = activityData.getUserPositiveTransactionsByCategory()
        val income : Double = if (incomeList == null) {
            0.0
        }else{
            incomeList!!.values.sum()
        }

        expenseList = activityData.getUserNegativeTransactionsByCategory()
        val expense = if (expenseList == null) {
            0.0
        }else{
            abs(expenseList!!.values.sum())
        }
        balanceGraph.add(PieEntry(income.toFloat(),"Income"))
        balanceGraph.add(PieEntry(expense.toFloat(),"Expense"))

        val pieDataSet = PieDataSet(balanceGraph,"")
        pieDataSet.setDrawValues(false)
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = color
        pieDataSet.valueTextSize = 16f

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.legend.isWordWrapEnabled = true
        pieChart.legend.textColor = color
        pieChart.legend.isEnabled = true
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        pieChart.description.isEnabled = false
        pieChart.centerText = "Balance"
        pieChart.setCenterTextColor(color)
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.animate()
        pieChart.setEntryLabelTextSize(12f)
        pieChart.holeRadius = 80f
        pieChart.setDrawEntryLabels(false)
        pieChart.contentDescription = ""
    }

}