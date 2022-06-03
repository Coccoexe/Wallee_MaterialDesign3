package com.example.md3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.data.entity.Transaction

class CardGoalAdapter(private val cards : List<Transaction>) :
    RecyclerView.Adapter<CardGoalAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var category : TextView
        var progressBar : ProgressBar

        init {
            category = itemView.findViewById(R.id.textCard)
            progressBar = itemView.findViewById(R.id.progBar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.goal_card, parent, false)

        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.category.text = cards[position].category
        //finire
    }
}