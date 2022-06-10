package com.example.md3.fragment.popup

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import com.example.md3.R
import com.example.md3.data.entity.Goal
import com.example.md3.fragment.GoalFragment
import com.example.md3.utility.IActivityData
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


class AddGoalPopup : DialogFragment() {
    private lateinit var activityData : IActivityData

    override fun onStart() {
        super.onStart()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dialog!!.window!!.setLayout(1400, 800)
        }
    }

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

        //date
        val calendar : Calendar = Calendar.getInstance()
        val format = SimpleDateFormat("EE d MMM yyyy", Locale.getDefault())
        var standardDate = format.format(calendar.time)
        val date : AppCompatImageView = inflateView.findViewById(R.id.selectDate)
        date.setOnClickListener{
            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())

            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints.build())
                .build()
            picker.show(requireActivity().supportFragmentManager,"datePicker")
            picker.addOnPositiveButtonClickListener {
                standardDate = format.format(picker.selection)
            }
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
        goalAmount.suffixText = activityData.getCurrency()

        //confirmButton
        val confirm : Button = inflateView.findViewById(R.id.confirm)
        confirm.setOnClickListener {
            val money: Double

            if (goalAmount.editText!!.text.isEmpty() || categoryMenu.editText!!.text.isEmpty()) {
                goalAmount.editText!!.text.clear()
                dismiss()
                Toast.makeText(context, "Gaol amount cannot be 0!", Toast.LENGTH_SHORT).show()
            } else {
                money = if (radiogroup.checkedButtonId == R.id.addButton){
                    goalAmount.editText!!.text.toString().toDouble()
                }else{
                    0.0 - goalAmount.editText!!.text.toString().toDouble()
                }
                val amount = if (money > 0.0){
                    "positive"
                }else{
                    "negative"
                }
                if (activityData.existGoal(categoryMenu.editText!!.text.toString(),standardDate,amount))
                {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Overwrite old Goal?")
                        .setMessage("A goal with same category already exist, do you want to overwrite it?")
                        .setNeutralButton("Cancel"){ dialog,which -> }
                        .setNegativeButton("Decline"){ dialog,which -> }
                        .setPositiveButton("Accept"){ dialog,which ->
                            activityData.insertGoal(
                                Goal(
                                    activityData.getGoalByCategory(categoryMenu.editText!!.text.toString(),standardDate,amount)!!.id,
                                    activityData.getEmail(),
                                    categoryMenu.editText!!.text.toString(),
                                    money,
                                    standardDate,
                                    false,
                                    true
                                )
                            )
                            updateView()
                        }
                        .show()
                }else {
                    activityData.insertGoal(
                        Goal(
                            0,
                            activityData.getEmail(),
                            categoryMenu.editText!!.text.toString(),
                            money,
                            standardDate,
                            false,
                            true
                        )
                    )
                    updateView()
                }
            }
        }

        //cancelButton
        val cancel : Button = inflateView.findViewById(R.id.cancel)
        cancel.setOnClickListener{
            dismiss()
        }

        return inflateView
    }

    private fun updateView()
    {
        (parentFragment as GoalFragment).getGoalList()
        (parentFragment as GoalFragment).setAdapter()
        dismiss()
    }
}