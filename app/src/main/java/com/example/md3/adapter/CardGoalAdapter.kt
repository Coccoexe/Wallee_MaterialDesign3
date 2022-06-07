package com.example.md3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.data.entity.Goal
import com.example.md3.fragment.GoalFragment

class CardGoalAdapter(private val cards : List<Goal>, var fragment: GoalFragment) :
    RecyclerView.Adapter<CardGoalAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryCard : TextView = itemView.findViewById(R.id.textCard)
        private val progressBar : ProgressBar = itemView.findViewById(R.id.progBar)
        private val points : TextView = itemView.findViewById(R.id.points)

        fun bind(goal: Goal) {
            var maximum = goal.sum
            if (maximum < 0)
                maximum = 0 - maximum

            var amount = fragment.getBalanceGoal(goal.category)
            if (amount < 0)
                amount = 0 - amount

            val percent = String.format("%.2f", (amount/maximum)*100)

            categoryCard.text = goal.category
            progressBar.max = maximum.toInt()
            progressBar.setProgress(amount.toInt())
            points.text = percent.plus("%")

            //points.text = amount.toString().plus("/").plus(maximum.toString())
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