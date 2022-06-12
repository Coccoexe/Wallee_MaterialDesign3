package com.example.md3.fragment.popup

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
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
            dialog!!.window!!.setLayout((resources.displayMetrics.widthPixels * 0.6).toInt(),(resources.displayMetrics.heightPixels * 0.9).toInt())
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
                .setTitleText(resources.getString(R.string.select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints.build())
                .build()
            picker.show(requireActivity().supportFragmentManager,"datePicker")
            picker.addOnPositiveButtonClickListener {
                standardDate = format.format(picker.selection)
            }
        }

        //menu spinner
        val categoryMenu : TextInputLayout = inflateView.findViewById(R.id.chooseCategory)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, resources.getStringArray(R.array.income))
        (categoryMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        //radioGroup
        val radioGroup : MaterialButtonToggleGroup = inflateView.findViewById(R.id.addOrRemove)
        radioGroup.check(R.id.addButton)
        radioGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked){

                val items = ArrayList<String>()
                (categoryMenu.editText as? AutoCompleteTextView)?.text?.clear()

                when (checkedId) {
                    R.id.addButton -> {
                        items.addAll(resources.getStringArray(R.array.income))
                    }
                    R.id.removeButton -> {
                        items.addAll(resources.getStringArray(R.array.expenses))
                    }
                }
                val menuAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                (categoryMenu.editText as? AutoCompleteTextView)?.setAdapter(menuAdapter)
            }
        }

        val goalAmount : TextInputLayout = inflateView.findViewById(R.id.chooseAmount)
        goalAmount.suffixText = activityData.getCurrency()
        goalAmount.editText!!.setOnFocusChangeListener{ view, hasFocus ->
            if (view.id == R.id.amountBox && !hasFocus){
                (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken,0)
            }
        }

        //confirmButton
        val confirm : Button = inflateView.findViewById(R.id.confirm)
        confirm.setOnClickListener {
            val money: Double

            if (goalAmount.editText!!.text.isEmpty() || categoryMenu.editText!!.text.isEmpty()) {
                goalAmount.editText!!.text.clear()
                dismiss()
                Toast.makeText(context, resources.getString(R.string.goal_amount_zero), Toast.LENGTH_SHORT).show()
            } else {
                money = if (radioGroup.checkedButtonId == R.id.addButton){
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
                        .setTitle(resources.getString(R.string.overwrite_goal))
                        .setMessage(resources.getString(R.string.goal_exist))
                        .setNeutralButton(resources.getString(R.string.cancel)){ _, _ -> }
                        .setPositiveButton(resources.getString(R.string.accept)){ _, _ ->
                            activityData.insertGoal(
                                Goal(
                                    activityData.getGoalByCategory(categoryMenu.editText!!.text.toString(),standardDate,amount)!!.id,
                                    activityData.getEmail(),
                                    categoryMenu.editText!!.text.toString(),
                                    money,
                                    standardDate,
                                    completed = false,
                                    toNotify = true
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
                            completed = false,
                            toNotify = true
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