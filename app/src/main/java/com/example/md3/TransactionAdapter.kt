package com.example.md3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.data.entity.Transaction

class TransactionAdapter(ctx: Context?, var transactionList : List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    var inflater: LayoutInflater
    var context = ctx

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = inflater.inflate(R.layout.transaction_card,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.setImageResource(getDrawable(transactionList[position].category))
        holder.amount.text = transactionList[position].amount.toString()
        holder.date.text = transactionList[position].date
        //holder.bind(transactionList[position])
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var image : ImageView
        var amount : TextView
        var date : TextView

        init {
            image = itemView.findViewById(R.id.lastImage)
            amount = itemView.findViewById(R.id.lastAmount)
            date = itemView.findViewById(R.id.lastDate)
        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
    }

    private fun getDrawable(category : String) : Int{
        val income = context!!.resources.getStringArray(R.array.income)
        val expense = context!!.resources.getStringArray(R.array.expenses)
        when(category){
            income[0] -> return R.drawable.salary
            income[1] -> return R.drawable.rent
            income[2] -> return R.drawable.investment
            income[3] -> return R.drawable.selling
            income[4] -> return R.drawable.gift
            income[5] -> return R.drawable.more
            expense[0] -> return R.drawable.bills
            expense[1] -> return R.drawable.grocery
            expense[2] -> return R.drawable.transportation
            expense[3] -> return R.drawable.home
            expense[4] -> return R.drawable.health
            expense[5] -> return R.drawable.gift
            expense[6] -> return R.drawable.more
        }
        return R.drawable.more
    }

}