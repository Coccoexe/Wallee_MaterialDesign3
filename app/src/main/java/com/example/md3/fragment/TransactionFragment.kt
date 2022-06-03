package com.example.md3.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.TransactionAdapter
import com.example.md3.data.entity.Transaction
import com.example.md3.events.IActivityData
import com.google.android.material.color.DynamicColors

class TransactionFragment : Fragment() {

    private lateinit var activityData : IActivityData
    private lateinit var dataList : RecyclerView
    private lateinit var adapter: TransactionAdapter
    private var transactionList : List<Transaction>? = null
    private lateinit var gridLayoutManager: GridLayoutManager

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

        //recyclerview
        transactionList = activityData.getUserWithTransaction()
        dataList = inflateView.findViewById(R.id.dataList)
        gridLayoutManager = GridLayoutManager(context,1,GridLayoutManager.VERTICAL,false)
        setAdapter()


        val all : AppCompatImageView = inflateView.findViewById(R.id.allTransactions)
        all.setImageResource(R.drawable.money_in_out_color)
        val positive : AppCompatImageView = inflateView.findViewById(R.id.positiveTransactions)
        val negative : AppCompatImageView = inflateView.findViewById(R.id.negativeTransactions)

        all.setOnClickListener{
            all.setImageResource(R.drawable.money_in_out_color)
            positive.setImageResource(R.drawable.money_in)
            negative.setImageResource(R.drawable.money_out)
            transactionList = activityData.getUserWithTransaction()
            setAdapter()
        }

        positive.setOnClickListener{
            all.setImageResource(R.drawable.money_in_out)
            positive.setImageResource(R.drawable.money_in_color)
            negative.setImageResource(R.drawable.money_out)
            transactionList = activityData.getUserWithTransactionPositive()
            setAdapter()
        }

        negative.setOnClickListener{
            all.setImageResource(R.drawable.money_in_out)
            positive.setImageResource(R.drawable.money_in)
            negative.setImageResource(R.drawable.money_out_color)
            transactionList = activityData.getUserWithTransactionNegative()
            setAdapter()
        }

        return inflateView
    }

    private fun setAdapter()
    {
        if (!transactionList.isNullOrEmpty()) {
            val adapter = TransactionAdapter(context, transactionList!!)
            dataList.layoutManager = gridLayoutManager
            dataList.adapter = adapter
        }
    }

}