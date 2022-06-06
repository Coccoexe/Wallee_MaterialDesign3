package com.example.md3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.data.entity.Goal
import com.example.md3.data.entity.Transaction
import com.example.md3.fragment.GoalFragment
import com.example.md3.fragment.TransactionFragment

class CardGoalAdapter(private val cards : List<Goal>, var fragment: GoalFragment) :
    RecyclerView.Adapter<CardGoalAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryCard : TextView = itemView.findViewById(R.id.textCard)
        var progressBar : ProgressBar = itemView.findViewById(R.id.progBar)

        fun bind(goal: Goal) {
            categoryCard.text = goal.category
            progressBar.progress = (fragment.getBalanceGoal(goal.category)/goal.sum).toInt()
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
        holder.bind(cards[position])
    }
}