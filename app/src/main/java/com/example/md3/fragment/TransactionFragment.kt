package com.example.md3.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.adapter.TransactionAdapter
import com.example.md3.data.entity.Transaction
import com.example.md3.events.IActivityData
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.android.material.divider.MaterialDivider
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionFragment : Fragment() {

    private lateinit var activityData : IActivityData
    private var transactionList : List<Transaction>? = null
    private lateinit var gridLayoutManager: GridLayoutManager
    private val format : SimpleDateFormat = SimpleDateFormat("EE d MMM yyyy, 'at' h:mm a",Locale.getDefault())
    private var filterToggle : Boolean = false
    private var color: Int = -1

    //view
    private lateinit var dataList : RecyclerView
    private lateinit var noTransaction : TextView
    private lateinit var filterMenu : AppCompatImageView
    private lateinit var divider : MaterialDivider
    private lateinit var transactionGroup : MaterialButtonToggleGroup
    private lateinit var dateGroup: MaterialButtonToggleGroup
    private lateinit var categoryGroup: RadioGroup
    private lateinit var categorySpinner: Spinner
    private lateinit var all : MaterialButton
    private lateinit var positive : MaterialButton
    private lateinit var negative : MaterialButton


    //amount -> "all", "positive", "negative"
    private lateinit var filterAmount : String
    //category -> list contain category filter
    private var filterCategory : ArrayList<String> = ArrayList()
    //date -> date in string with format
    private var filterDate : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflateView = inflater.inflate(R.layout.fragment_transaction, container, false)
        DynamicColors.applyToActivityIfAvailable(requireActivity())

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        //filter View
        filterMenu = inflateView.findViewById(R.id.filterMenu)
        divider = inflateView.findViewById(R.id.divider)
        filterToggle = false
        //amount
        transactionGroup = inflateView.findViewById(R.id.selectTransaction)
        all = inflateView.findViewById(R.id.all_transaction)
        positive = inflateView.findViewById(R.id.positive_transaction)
        negative = inflateView.findViewById(R.id.negative_transaction)
        color = MaterialColors.getColor(inflateView,com.google.android.material.R.attr.colorOnSecondaryContainer)
        //date
        dateGroup = inflateView.findViewById(R.id.selectDate)
        //category
        categoryGroup = inflateView.findViewById(R.id.selectCategory)
        categorySpinner = inflateView.findViewById(R.id.categorySpinner)


        //default -------------------------------------------------------

            //divider
        divider.visibility = View.GONE

            //amount
        filterAmount = "all"
        //filterCategory is empty
        //filterDate is null
        transactionGroup.check(R.id.all_transaction)
        all.iconTint = null
        all.setIconResource(R.drawable.money_in_out_color)

            //date
        dateGroup.visibility = View.GONE
        dateGroup.check(R.id.all_time)

            //category
        categoryGroup.visibility = View.GONE
        categoryGroup.check(R.id.all)
        categorySpinner.visibility = View.GONE

            //recyclerview
        dataList = inflateView.findViewById(R.id.dataList)
            //no view available
        noTransaction = inflateView.findViewById(R.id.no_transaction)
        noTransaction.visibility = View.INVISIBLE
            //gridLayout
        gridLayoutManager = GridLayoutManager(context,1,GridLayoutManager.VERTICAL,false)
            //adapter
        getTransactionList()
        setAdapter()


        // ---------------------------------------------------------------

        //listener
        filterMenu.setOnClickListener{
            if (filterToggle)
            {
                filterMenu.setImageResource(R.drawable.ic_filter_down_24)
                divider.visibility = View.GONE
                dateGroup.visibility = View.GONE
                categoryGroup.visibility = View.GONE
                categorySpinner.visibility = View.GONE

                filterToggle = false
            }
            else{
                filterMenu.setImageResource(R.drawable.ic_filter_up_24)
                divider.visibility = View.VISIBLE
                dateGroup.visibility = View.VISIBLE
                categoryGroup.visibility = View.VISIBLE
                if (categoryGroup.checkedRadioButtonId == R.id.custom)
                {
                    categorySpinner.visibility = View.VISIBLE
                }
                else{
                    categorySpinner.visibility = View.INVISIBLE
                }
                filterToggle = true
            }
        }

        transactionGroup.addOnButtonCheckedListener() { trasactionGroup, chekedId, isChecked ->
            if (isChecked){
                when(chekedId){
                    R.id.all_transaction -> {
                        filterAmount = "all"

                        all.iconTint = null
                        all.setIconResource(R.drawable.money_in_out_color)

                        positive.iconTint = ColorStateList.valueOf(color)
                        positive.setIconResource(R.drawable.money_in)

                        negative.iconTint = ColorStateList.valueOf(color)
                        negative.setIconResource(R.drawable.money_out)

                        if (categoryGroup.checkedRadioButtonId == R.id.custom) {
                            val items = ArrayList<String>()
                            items.addAll(resources.getStringArray(R.array.income))
                            items.addAll(resources.getStringArray(R.array.expenses))
                            val adapter: Any =
                                ArrayAdapter<Any?>(
                                    requireContext(), android.R.layout.simple_spinner_dropdown_item,
                                    items as List<Any?>
                                )
                            categorySpinner.adapter = adapter as SpinnerAdapter?
                            filterCategory.clear()
                            filterCategory.add(categorySpinner.selectedItem.toString())
                            categorySpinner.visibility = View.VISIBLE
                        }

                    }
                    R.id.positive_transaction -> {
                        filterAmount = "positive"

                        all.iconTint = ColorStateList.valueOf(color)
                        all.setIconResource(R.drawable.money_in_out)

                        positive.iconTint = null
                        positive.setIconResource(R.drawable.money_in_color)

                        negative.iconTint = ColorStateList.valueOf(color)
                        negative.setIconResource(R.drawable.money_out)

                        if (categoryGroup.checkedRadioButtonId == R.id.custom) {
                            val items = ArrayList<String>()
                            items.addAll(resources.getStringArray(R.array.income))
                            val adapter: Any =
                                ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                                    items as List<Any?>
                                )
                            categorySpinner.adapter = adapter as SpinnerAdapter?
                            filterCategory.clear()
                            filterCategory.add(categorySpinner.selectedItem.toString())
                            categorySpinner.visibility = View.VISIBLE
                        }
                    }
                    R.id.negative_transaction -> {
                        filterAmount = "negative"

                        all.iconTint = ColorStateList.valueOf(color)
                        all.setIconResource(R.drawable.money_in_out)

                        positive.iconTint = ColorStateList.valueOf(color)
                        positive.setIconResource(R.drawable.money_in)

                        negative.iconTint = null
                        negative.setIconResource(R.drawable.money_out_color)

                        if (categoryGroup.checkedRadioButtonId == R.id.custom) {
                            val items = ArrayList<String>()
                            items.addAll(resources.getStringArray(R.array.expenses))
                            val adapter: Any =
                                ArrayAdapter<Any?>(
                                    requireContext(), android.R.layout.simple_spinner_dropdown_item,
                                    items as List<Any?>
                                )
                            categorySpinner.adapter = adapter as SpinnerAdapter?
                            filterCategory.clear()
                            filterCategory.add(categorySpinner.selectedItem.toString())
                            categorySpinner.visibility = View.VISIBLE
                        }
                    }
                }
            }
            getTransactionList()
            setAdapter()
        }

        dateGroup.addOnButtonCheckedListener() { dateGroup, chekedId, isChecked ->
            if (isChecked){
                when (chekedId) {
                    R.id.all_time -> {
                        filterDate = null
                    }
                    R.id.last_year -> {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.YEAR, -1)
                        filterDate = format.format(calendar.time)
                    }
                    R.id.last_three_month -> {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.MONTH, -3)
                        filterDate = format.format(calendar.time)
                    }
                    R.id.last_month -> {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.MONTH, -1)
                        filterDate = format.format(calendar.time)
                    }
                }
            }
            getTransactionList()
            setAdapter()
        }

        categoryGroup.setOnCheckedChangeListener{ _, optionId ->
            run {
                when (optionId) {
                    R.id.all -> {
                        filterCategory.clear()

                        categorySpinner.visibility = View.INVISIBLE
                    }
                    R.id.custom -> {

                        val items = ArrayList<String>()

                        when(filterAmount){
                            "all" -> {
                                items.addAll(resources.getStringArray(R.array.income))
                                items.addAll(resources.getStringArray(R.array.expenses))

                            }
                            "positive" -> {
                                items.addAll(resources.getStringArray(R.array.income))
                            }
                            "negative" -> {
                                items.addAll(resources.getStringArray(R.array.expenses))
                            }
                        }
                        val adapter: Any =
                            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                                items as List<Any?>
                            )
                        categorySpinner.adapter = adapter as SpinnerAdapter?
                        filterCategory.clear()
                        filterCategory.add(categorySpinner.selectedItem.toString())
                        if (filterToggle) {
                            categorySpinner.visibility = View.VISIBLE
                        }
                        else{
                            categorySpinner.visibility = View.GONE
                        }
                    }
                }
            }
            getTransactionList()
            setAdapter()
        }

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filterCategory.clear()
                filterCategory.add(categorySpinner.selectedItem.toString())
                getTransactionList()
                setAdapter()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        return inflateView
    }

    private fun setAdapter() {
        if (!transactionList.isNullOrEmpty()) {
            noTransaction.visibility = View.INVISIBLE
            val adapter = TransactionAdapter(context, transactionList!!)
            dataList.layoutManager = gridLayoutManager
            dataList.adapter = adapter
        }
        else{
            noTransaction.visibility = View.VISIBLE
            dataList.adapter = null
        }
    }

    private fun getTransactionList(){

        transactionList = activityData.getUserWithTransactionFiltered(filterAmount,filterCategory,filterDate)
    }

}