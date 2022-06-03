package com.example.md3.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.TransactionAdapter
import com.example.md3.data.entity.Transaction
import com.example.md3.events.IActivityData
import com.google.android.material.color.DynamicColors

class TransactionFragment : Fragment() {

    private lateinit var activityData : IActivityData

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


        val transactionList : List<Transaction>? = activityData.getUserWithTransaction()
        val dataList : RecyclerView = inflateView.findViewById(R.id.dataList)
        val gridLayoutManager = GridLayoutManager(context,1,GridLayoutManager.VERTICAL,false)
        if (!transactionList.isNullOrEmpty()) {
            val adapter = TransactionAdapter(context, transactionList!!)
            dataList.layoutManager = gridLayoutManager
            dataList.adapter = adapter
        }

        return inflateView
    }

}