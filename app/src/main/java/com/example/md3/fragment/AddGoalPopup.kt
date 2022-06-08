package com.example.md3.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.md3.R
import com.example.md3.data.entity.Goal
import com.example.md3.events.IActivityData
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputLayout
import java.util.ArrayList


class AddGoalPopup : DialogFragment() {
    private lateinit var activityData : IActivityData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {

        //inflater
        val inflateView = inflater.inflate(R.layout.add_goal, container, false)

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        //menu tendina
        val categoryMenu : TextInputLayout = inflateView.findViewById(R.id.chooseCategory)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, resources.getStringArray(R.array.income))
        (categoryMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        //radiogroup
        val radiogroup : MaterialButtonToggleGroup = inflateView.findViewById(R.id.addOrRemove)
        radiogroup.check(R.id.addButton)
        radiogroup.addOnButtonCheckedListener() { dateGroup, chekedId, isChecked ->
            if(isChecked){

                val items = ArrayList<String>()
                (categoryMenu.editText as? AutoCompleteTextView)?.text?.clear()

                when (chekedId) {
                    R.id.addButton -> {
                        items.addAll(resources.getStringArray(R.array.income))
                    }
                    R.id.removeButton -> {
                        items.addAll(resources.getStringArray(R.array.expenses))
                    }
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                (categoryMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        }

        val goalAmount : TextInputLayout = inflateView.findViewById(R.id.chooseAmount)

        //confirmButton
        val confirm : Button = inflateView.findViewById(R.id.confirm)
        confirm.setOnClickListener {
            var money = 0.0

            if (goalAmount.editText!!.text.isEmpty() || categoryMenu.editText!!.text.isEmpty()) {
                goalAmount.editText!!.text.clear()
                Toast.makeText(context, "Transaction amount cannot be 0!", Toast.LENGTH_SHORT).show()
            } else {
                money = goalAmount.editText!!.text.toString().toDouble()
                activityData.insertGoal(
                    Goal(
                        0,
                        categoryMenu.editText!!.text.toString(),
                        money
                    )
                )
                (parentFragment as GoalFragment).getGoalList()
                (parentFragment as GoalFragment).setAdapter()
            }

            dismiss()
        }

        //cancelButton
        val cancel : Button = inflateView.findViewById(R.id.cancel)
        cancel.setOnClickListener{
            dismiss()
        }

        return inflateView
    }
}