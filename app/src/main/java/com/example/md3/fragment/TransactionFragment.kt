package com.example.md3.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.TransactionAdapter
import com.example.md3.data.entity.Transaction
import com.example.md3.events.IActivityData
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionFragment : Fragment() {

    private lateinit var activityData : IActivityData
    private lateinit var dataList : RecyclerView
    private lateinit var noTransaction : TextView
    private var transactionList : List<Transaction>? = null
    private lateinit var gridLayoutManager: GridLayoutManager
    private val format : SimpleDateFormat = SimpleDateFormat("EE d MMM yyyy, 'at' h:mm a",Locale.getDefault())

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
            //amount
        val all : AppCompatImageView = inflateView.findViewById(R.id.allTransactions)
        val positive : AppCompatImageView = inflateView.findViewById(R.id.positiveTransactions)
        val negative : AppCompatImageView = inflateView.findViewById(R.id.negativeTransactions)
                //color
        val color : Int = MaterialColors.getColor(inflateView,com.google.android.material.R.attr.colorOnSurface)
            //date
        val dateGroup: RadioGroup = inflateView.findViewById(R.id.selectDate)
            //category
        val categoryGroup: RadioGroup = inflateView.findViewById(R.id.selectCategory)
        val categorySpinner: Spinner = inflateView.findViewById(R.id.categorySpinner)


        //default -------------------------------------------------------

            //amount
        filterAmount = "all"
        //filterCategory is empty
        //filterDate is null
        all.setImageResource(R.drawable.money_in_out_color)
        positive.setColorFilter(color)
        negative.setColorFilter(color)

            //date
        dateGroup.check(R.id.all_time)

            //category
        categoryGroup.check(R.id.all)
        categorySpinner.visibility = View.INVISIBLE

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
        all.setOnClickListener{
            //manage filter
            all.clearColorFilter()
            positive.setColorFilter(color)
            negative.setColorFilter(color)

            //manage image
            all.setImageResource(R.drawable.money_in_out_color)
            positive.setImageResource(R.drawable.money_in)
            negative.setImageResource(R.drawable.money_out)

            filterAmount = "all"

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
            getTransactionList()

            setAdapter()
        }

        positive.setOnClickListener{
            //manage filter
            all.setColorFilter(color)
            positive.clearColorFilter()
            negative.setColorFilter(color)

            //manage image
            all.setImageResource(R.drawable.money_in_out)
            positive.setImageResource(R.drawable.money_in_color)
            negative.setImageResource(R.drawable.money_out)

            filterAmount = "positive"

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

            getTransactionList()

            setAdapter()
        }

        negative.setOnClickListener{
            //manage filter
            all.setColorFilter(color)
            positive.setColorFilter(color)
            negative.clearColorFilter()

            //manage image
            all.setImageResource(R.drawable.money_in_out)
            positive.setImageResource(R.drawable.money_in)
            negative.setImageResource(R.drawable.money_out_color)

            filterAmount = "negative"

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
            getTransactionList()

            setAdapter()
        }

        dateGroup.setOnCheckedChangeListener { _, optionId ->
            run {
                when (optionId) {
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
                        when(filterAmount){
                            "all" -> {
                                val items = ArrayList<String>()
                                items.addAll(resources.getStringArray(R.array.income))
                                items.addAll(resources.getStringArray(R.array.expenses))
                                val adapter: Any =
                                    ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                                        items as List<Any?>
                                    )
                                categorySpinner.adapter = adapter as SpinnerAdapter?
                                filterCategory.clear()
                                filterCategory.add(categorySpinner.selectedItem.toString())
                                categorySpinner.visibility = View.VISIBLE
                            }
                            "positive" -> {
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
                            "negative" -> {
                                val items = ArrayList<String>()
                                items.addAll(resources.getStringArray(R.array.expenses))
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

    private fun setAdapter()
    {
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