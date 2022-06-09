package com.example.md3.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.R
import com.example.md3.data.entity.Goal
import com.example.md3.fragment.GoalFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlin.math.abs

class CardGoalAdapter(ctx: Context?, goalList : List<Goal>, var fragment: GoalFragment) :
    RecyclerView.Adapter<CardGoalAdapter.CardViewHolder>() {

    var inflater: LayoutInflater
    var context = ctx
    private var cards = goalList

    var selectionMode = false
    var selected : ArrayList<Int>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.goal_card, parent, false)

        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.goal = cards[position]
        holder.bind()
        holder.selectionUpdateView()
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnLongClickListener, View.OnClickListener{
        private val categoryCard : TextView
        private var progressBar : LinearProgressIndicator
        private val points : TextView
        private var selectedImage : ImageView
        private val date : TextView
        private val goalCard : MaterialCardView
        private val completedText : TextView

        private var balace = 0.0
        private lateinit var filterAmount : String

        lateinit var goal : Goal

        init{
            categoryCard = itemView.findViewById(R.id.textCard)
            progressBar = itemView.findViewById(R.id.progBar)
            points = itemView.findViewById(R.id.points)
            selectedImage = itemView.findViewById(R.id.selectedGoal)
            date = itemView.findViewById(R.id.date)
            goalCard = itemView.findViewById(R.id.card)
            completedText = itemView.findViewById(R.id.goalReached)
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }

        fun bind() {
            categoryCard.text = goal.category
            date.text = goal.date
            progressBar.max = abs(goal.sum).toInt()
            filterAmount = if (goal.sum > 0){
                "positive"
            }else{
                "negative"
            }
            balace = abs(fragment.getBalanceGoal(filterAmount,goal.category,goal.date))
            progressBar.progress = balace.toInt()
            if (balace >= abs(goal.sum).toInt()) {
                goalCard.setCardBackgroundColor(MaterialColors.getColor(itemView,com.google.android.material.R.attr.colorError))
                categoryCard.setTextColor(MaterialColors.getColor(itemView,com.google.android.material.R.attr.colorOnError))
                date.setTextColor(MaterialColors.getColor(itemView,com.google.android.material.R.attr.colorOnError))
                points.setTextColor(MaterialColors.getColor(itemView,com.google.android.material.R.attr.colorOnError))
                progressBar.visibility = View.INVISIBLE
                completedText.visibility = View.VISIBLE
            }

            points.text = "%s / %s".format(
                fragment.getFormattedMoney(balace),
                fragment.getFormattedMoney(goal.sum)
            )
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
            if (goal.isSelected){
                selectedImage.visibility = View.VISIBLE
            }else{
                selectedImage.visibility = View.INVISIBLE
            }
            fragment.updateContextBarTitle(selected.size)
        }

        private fun updateView(){
            if (goal.isSelected) {
                selected.remove(goal.id)
                goal.isSelected = false
                selectedImage.visibility = View.INVISIBLE
            } else {
                selected.add(goal.id)
                goal.isSelected = true
                selectedImage.visibility = View.VISIBLE
            }
            fragment.updateContextBarTitle(selected.size)
        }
    }

    init{
        inflater = LayoutInflater.from(ctx)
        selected = ArrayList()
        selected.clear()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectionAll(){
        if (selected.size < itemCount) {
            for (t in cards) {
                if (!t.isSelected) {
                    t.isSelected = true
                    selected.add(t.id)
                }
            }
        }else{
            for (t in cards) {
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
}