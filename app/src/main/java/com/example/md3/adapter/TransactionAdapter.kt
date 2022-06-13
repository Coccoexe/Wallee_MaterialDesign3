package com.example.md3.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.data.entity.Transaction
import com.example.md3.fragment.TransactionFragment

class TransactionAdapter(ctx: Context?, transList : List<Transaction>,
                         var fragment: TransactionFragment
) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    var inflater: LayoutInflater
    var context = ctx
    private var transactionList = transList.reversed()

    //variabili per la gestione della selezione multipla dei vari oggetti della lista
    var selectionMode = false
    var selected : ArrayList<Int>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = inflater.inflate(R.layout.transaction_card,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.setImageResource(getDrawable(transactionList[position].category))
        holder.amount.text = fragment.getFormattedMoney(transactionList[position].amount)
        holder.date.text = transactionList[position].date

        holder.transaction = transactionList[position]
        holder.selectionUpdateView()
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener, View.OnClickListener{

        var image : ImageView
        var amount : TextView
        var date : TextView
        private var selectedImage : ImageView

        lateinit var transaction : Transaction

        init {
            image = itemView.findViewById(R.id.lastImage)
            amount = itemView.findViewById(R.id.lastAmount)
            date = itemView.findViewById(R.id.lastDate)
            selectedImage = itemView.findViewById(R.id.selectedTransaction)
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onLongClick(p0: View?): Boolean {
            if (selected.isEmpty()){
                fragment.openContextBar()
                selectionMode = true
                updateView()
            } else {
                updateView()
            }

            if (selected.isEmpty()){
                fragment.closeContextBar()
                selectionMode = false
            }
            return true
        }

        override fun onClick(p0: View?) {
            if(selectionMode) {
               updateView()
            }
            if (selected.isEmpty()){
                fragment.closeContextBar()
                selectionMode = false
            }
        }

        fun selectionUpdateView(){
            if (transaction.isSelected){
                selectedImage.visibility = View.VISIBLE
            }else{
                selectedImage.visibility = View.INVISIBLE
            }
            fragment.updateContextBarTitle(selected.size)
        }

        private fun updateView(){
            if (transaction.isSelected) {
                selected.remove(transaction.id)
                transaction.isSelected = false
                selectedImage.visibility = View.INVISIBLE
            } else {
                selected.add(transaction.id)
                transaction.isSelected = true
                selectedImage.visibility = View.VISIBLE
            }
            fragment.updateContextBarTitle(selected.size)
        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
        selected = ArrayList()
        selected.clear()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectionAll(){
        if (selected.size < itemCount) {
            for (t in transactionList) {
                if (!t.isSelected) {
                    t.isSelected = true
                    selected.add(t.id)
                }
            }
        }else{
            for (t in transactionList) {
                if (t.isSelected) {
                    t.isSelected = false
                }
            }
            selected.clear()
            selectionMode = true
            fragment.closeContextBar()
        }
        //all list updated
        this.notifyDataSetChanged()
    }

    private fun getDrawable(category : String) : Int{
        return fragment.getDrawable(category)
    }

}