package com.example.md3.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.md3.R
import com.example.md3.data.entity.Transaction
import com.example.md3.fragment.popup.AddTransPopup
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
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class MainFragment : Fragment() {

    private lateinit var activityData : IActivityData
    private val format : SimpleDateFormat = SimpleDateFormat("EE d MMM yyyy",Locale.getDefault())

    private lateinit var welcome : TextView
    private lateinit var balance : TextView
    private lateinit var dateGroup : MaterialButtonToggleGroup
    private lateinit var addTrans : FloatingActionButton
    private lateinit var lastAmount : TextView
    private lateinit var lastDate : TextView
    private lateinit var lastImage : ImageView
    private lateinit var helpButton : ImageView
    private lateinit var pieChart : PieChart
    private lateinit var legend : TextView

    private var trans : List<Transaction>? = null
    private var color : Int = -1
    private var legendEntry : String = ""

    private var filterDate : String? = null
    private lateinit var colors : ArrayList<Int>
    private lateinit var balanceGraph : ArrayList<PieEntry>
    private var incomeList: List<Transaction>? = null
    private var expenseList: List<Transaction>? = null
    private var selectedSlice : String? = null

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
        dateGroup = inflateView.findViewById(R.id.selectDate)
        addTrans= inflateView.findViewById(R.id.addTransaction)
        lastAmount = inflateView.findViewById(R.id.lastAmount)
        lastDate = inflateView.findViewById(R.id.lastDate)
        lastImage  = inflateView.findViewById(R.id.lastImage)
        helpButton = inflateView.findViewById(R.id.help)
        pieChart = inflateView.findViewById(R.id.balanceChart)
        legend = inflateView.findViewById(R.id.legendChart)

        //default
        welcome.text = getString(R.string.user_welcome).format(activityData.getUserName())
        balance.text = activityData.formatMoney(activityData.getUserBalance())
        dateGroup.check(R.id.all_time)
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

        //initialize chart
        getChartData()
        activityData.checkCompletedGoal()
        activityData.updateBadge()

        //listener
        //applica il filtro data
        dateGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked){
                when (checkedId) {
                    R.id.all_time -> {
                        filterDate = null
                    }
                    R.id.last_month -> {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.MONTH, -1)
                        filterDate = format.format(calendar.time)
                    }
                }
            }
            updateView()
        }

        //crea il popup per aggiungere una transazione
        addTrans.setOnClickListener{
            val popup = AddTransPopup()
            //popup.show(requireActivity().supportFragmentManager, "popupTransaction")
            popup.show(childFragmentManager, "popupTransaction")
        }

        //dialog aiuto a leggere il grafico
        helpButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.help_reading_chart))
                .setMessage(resources.getString(R.string.help_pie))
                .setPositiveButton(resources.getString(R.string.thanks)) { _, _ ->
                    // Respond to positive button press
                }
                .show()
        }

        //grafico
        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                legendEntry = ""
                when((e as PieEntry).label){
                    resources.getString(R.string.available) -> {
                        selectedSlice = resources.getString(R.string.available)
                        legendEntry = updateLegend(resources.getString(R.string.available))
                    }
                    resources.getString(R.string.expense) -> {
                        selectedSlice = resources.getString(R.string.expense)
                        legendEntry = updateLegend(resources.getString(R.string.expense))
                    }
                }
                legend.text = legendEntry
                legend.visibility = View.VISIBLE
            }

            override fun onNothingSelected() {
                selectedSlice = null
                legendEntry = updateLegend(null)
                legend.visibility = View.GONE
            }
        })

        // Inflate the layout for this fragment
        return inflateView
    }

    fun updateLegend(selected : String?) : String{
        if (selected != null) {
            when (selected) {
                resources.getString(R.string.available) -> {
                    return resources.getString(R.string.available_balance).format(activityData.formatMoney(incomeList!!.sumOf { it.amount } + expenseList!!.sumOf { it.amount }))
                }
                resources.getString(R.string.expense) -> {
                    var ret = resources.getString(R.string.expense) + '\n'
                    for (category in resources.getStringArray(R.array.expenses)) {
                        val negativeList = activityData.getUserWithTransactionFiltered(
                            "negative",
                            category,
                            filterDate
                        )
                        if (negativeList.isNotEmpty()) {
                            var amount = 0.0
                            for (item in negativeList) {
                                amount += item.amount
                            }
                            ret += "$category : %s\n".format(activityData.formatMoney(amount))
                        }
                    }
                    return ret
                }
            }
        }
        return ""
    }

    fun updateView(){
        //update badge
        activityData.checkCompletedGoal()
        activityData.updateBadge()

        getChartData()
        balance.text = activityData.formatMoney(incomeList!!.sumOf { it.amount } + expenseList!!.sumOf { it.amount })
        legend.text = updateLegend(selectedSlice)
        pieChart.notifyDataSetChanged()
        pieChart.invalidate()
        pieChart.animateXY(500,500)
    }

    private fun getChartData(){
        balanceGraph = ArrayList()
        incomeList = activityData.getUserWithTransactionFiltered("positive",null,filterDate)
        val income = if (incomeList.isNullOrEmpty()){
            0.0
        }else{
            incomeList!!.sumOf { it.amount }
        }

        expenseList = activityData.getUserWithTransactionFiltered("negative",null,filterDate)
        val expense = if (expenseList.isNullOrEmpty()) {
            0.0
        }else{
            abs(expenseList!!.sumOf { it.amount })
        }

        val balance = if (expense > income){
            0.0
        }else{
            income - expense
        }

        if (incomeList!!.isNotEmpty() || expenseList!!.isNotEmpty()) {
            colors.clear()
            colors.add(ContextCompat.getColor(requireContext(),R.color.color_green_chart))
            colors.add(ContextCompat.getColor(requireContext(),R.color.color_red_chart))
            balanceGraph.add(PieEntry(balance.toFloat(), resources.getString(R.string.available)))
            balanceGraph.add(PieEntry(expense.toFloat(), resources.getString(R.string.expense)))
        }else{
            colors.clear()
            colors.add(ContextCompat.getColor(requireContext(),R.color.color_empty_chart))
            balanceGraph.add(PieEntry(1f,resources.getString(R.string.no_transaction_found)))
        }

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
        pieChart.centerText = resources.getString(R.string.balance)
        pieChart.setCenterTextColor(color)
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.animate()
        pieChart.setEntryLabelTextSize(12f)
        pieChart.holeRadius = 80f
        pieChart.setDrawEntryLabels(false)
        pieChart.contentDescription = ""
    }

}