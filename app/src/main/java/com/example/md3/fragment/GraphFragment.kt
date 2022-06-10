package com.example.md3.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.md3.R
import com.example.md3.utility.IActivityData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList


class GraphFragment : Fragment() {

    private lateinit var activityData : IActivityData
    private val format : SimpleDateFormat = SimpleDateFormat("EE d MMM yyyy",Locale.getDefault())
    private var filterToggle : Boolean = false
    private var filterDate : String? = null
    private var color = -1
    private lateinit var colors : ArrayList<Int>

    //view
    private lateinit var topAppBar : MaterialToolbar
    private lateinit var filterBar : ConstraintLayout
    private lateinit var filterDateText : TextView
    private lateinit var dateGroup : MaterialButtonToggleGroup
    private lateinit var all : MaterialButton
    private lateinit var month : MaterialButton
    private lateinit var balanceChart : BarChart
    private lateinit var positiveChart : RadarChart
    private lateinit var negativeChart : RadarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflateView = inflater.inflate(R.layout.fragment_graph, container, false)
        DynamicColors.applyToActivityIfAvailable(requireActivity())

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        //initialize
        topAppBar = inflateView.findViewById(R.id.topAppBarGraph)
        filterBar = inflateView.findViewById(R.id.filterBarGraph)
        filterDateText = inflateView.findViewById(R.id.dateFilterTextGraph)
        dateGroup = inflateView.findViewById(R.id.selectDate)
        all = inflateView.findViewById(R.id.all_time)
        month = inflateView.findViewById(R.id.last_month)
        balanceChart = inflateView.findViewById(R.id.balanceChart)
        positiveChart = inflateView.findViewById(R.id.positiveChart)
        negativeChart = inflateView.findViewById(R.id.negativeChart)

        //default
        filterBar.visibility = View.GONE
        filterDateText.visibility = View.GONE
        dateGroup.visibility = View.GONE
        dateGroup.check(R.id.all_time)
        color = MaterialColors.getColor(inflateView,com.google.android.material.R.attr.colorOnSecondaryContainer)
        colors = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS){
            colors.add(color)
        }
        updateView()

        //listener
        topAppBar.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId){
                R.id.filterMenu -> {
                    if (filterToggle)
                    {
                        topAppBar.menu.getItem(0).setIcon(R.drawable.ic_filter_down_24)
                        filterBar.visibility = View.GONE
                        filterDateText.visibility = View.GONE
                        dateGroup.visibility = View.GONE

                        filterToggle = false
                    }
                    else{
                        topAppBar.menu.getItem(0).setIcon(R.drawable.ic_filter_up_24)
                        filterBar.visibility = View.VISIBLE
                        filterDateText.visibility = View.VISIBLE
                        dateGroup.visibility = View.VISIBLE

                        filterToggle = true
                    }
                    true
                }
                else -> false
            }
        }

        dateGroup.addOnButtonCheckedListener() { dateGroup, chekedId, isChecked ->
            if (isChecked){
                when (chekedId) {
                    R.id.all_time -> {
                        filterDate = null
                    }
                    R.id.last_month -> {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.MONTH, -1)
                        filterDate = format.format(calendar.time)
                    }
                }
                updateView()
            }
        }

        return inflateView
    }

    private fun updateView(){
        drawChartBalance()
        drawChartPositive()
        drawChartNegative()
    }

    private fun drawChartBalance(){
        val transactionList = activityData.getUserWithTransactionFiltered("all",null,filterDate)
        val entry : ArrayList<BarEntry> = ArrayList()
        val formatArray : ArrayList<String> = ArrayList()
        val calendar = Calendar.getInstance()


        if (filterDate != null) {
            //controllo ogni transazione, sommo i giorni uguali

            val day = SimpleDateFormat("dd", Locale.getDefault())

            val monthBalance : MutableMap<Int, Double> =
                (1..LocalDate.now().lengthOfMonth()).toList().associateBy({it},{0.0}).toMutableMap()

            for (t in transactionList){
                monthBalance[day.format(format.parse(t.date)!!).toInt()] =
                    monthBalance[day.format(format.parse(t.date)!!).toInt()]!!.plus(t.amount)
            }
            for (i in monthBalance) {
                formatArray.add(i.key.toString())
                entry.add(BarEntry(i.key.toFloat(),i.value.toFloat()))
            }

        }else{
            //controllo ogni transazione, sommo i mesi uguali

            val month = SimpleDateFormat("MMM yyyy",Locale.getDefault())
            val monthList : ArrayList<String> = ArrayList()
            val now = calendar.time
            calendar.time = transactionList.minOf { format.parse(it.date) }
            while (calendar.time <= now){
                monthList.add(month.format(calendar.time))
                calendar.add(Calendar.MONTH,1)
            }

            val allBalance : MutableMap<String,Double> = monthList.associateBy({it}, {0.0}).toMutableMap()

            for (t in transactionList){
                if (allBalance[month.format(format.parse(t.date)!!)] == null){
                    allBalance[month.format(format.parse(t.date)!!)] = 0.0
                }
                allBalance[month.format(format.parse(t.date)!!)] = allBalance[month.format(format.parse(t.date)!!)]!!.plus(t.amount)
            }
            for (i in allBalance) {
                formatArray.add(i.key)
                entry.add(BarEntry(formatArray.indexOf(i.key).toFloat(),i.value.toFloat()))
            }
        }

        val barDataSet = BarDataSet(entry,"Balance")
        barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        barDataSet.colors = colors
        barDataSet.setDrawValues(false)
        barDataSet.valueTextColor = color
        barDataSet.valueTextSize = 16f

        val barData = BarData(barDataSet)

        balanceChart.setFitBars(true)
        balanceChart.axisRight.setDrawLabels(false)
        balanceChart.axisRight.setDrawGridLines(false)
        balanceChart.axisLeft.textColor = color
        balanceChart.xAxis.setDrawGridLines(false)
        balanceChart.xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
        balanceChart.xAxis.textColor = color
        balanceChart.xAxis.granularity = 1f
        balanceChart.xAxis.valueFormatter = IndexAxisValueFormatter(formatArray)
        balanceChart.legend.isEnabled = false
        balanceChart.description.isEnabled = false
        balanceChart.data = barData
        balanceChart.animateY(2000)

    }

    private fun drawChartPositive(){

    }

    private fun drawChartNegative(){

    }

}