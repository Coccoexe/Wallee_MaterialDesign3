package com.example.md3.fragment.popup


import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import com.example.md3.R
import com.example.md3.data.entity.Transaction
import com.example.md3.fragment.MainFragment
import com.example.md3.utility.IActivityData
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


class AddTransPopup : DialogFragment() {

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
    ): View? {

        //inflater
        val inflateView =  inflater.inflate(R.layout.trans_popup, container, false)

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        if (dialog != null && dialog?.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }

        //date
        val calendar : Calendar = Calendar.getInstance()
        val format = SimpleDateFormat("EE d MMM yyyy",Locale.getDefault())
        var standardDate = format.format(calendar.time)
        val date : AppCompatImageView = inflateView.findViewById(R.id.selectDate)
        date.setOnClickListener{
            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())

            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(constraints.build())
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            picker.show(requireActivity().supportFragmentManager,"datePicker")
            picker.addOnPositiveButtonClickListener {
                standardDate = format.format(picker.selection)
            }
        }

        //menu spinner
        val categoryMenu: TextInputLayout = inflateView.findViewById(R.id.categoryMenu)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, resources.getStringArray(R.array.income))
        (categoryMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        //radioGroup
        val radioGroup : MaterialButtonToggleGroup = inflateView.findViewById(R.id.selectTransaction)
        radioGroup.check(R.id.add)
        radioGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked){

                val items = ArrayList<String>()
                (categoryMenu.editText as? AutoCompleteTextView)?.text?.clear()

                when (checkedId) {
                    R.id.add -> {
                        items.addAll(resources.getStringArray(R.array.income))
                    }
                    R.id.remove -> {
                        items.addAll(resources.getStringArray(R.array.expenses))
                    }
                }
                val menuAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                (categoryMenu.editText as? AutoCompleteTextView)?.setAdapter(menuAdapter)
            }
        }

        //text amount
        val amount : TextInputLayout = inflateView.findViewById(R.id.amount)
        amount.suffixText = activityData.getCurrency()
        amount.editText!!.setOnFocusChangeListener{ view, hasFocus ->
            if (view.id == R.id.amountBox && !hasFocus){
                (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken,0)
            }
        }

        //confirmButton
        val confirm : Button = inflateView.findViewById(R.id.popupOk)
        confirm.setOnClickListener{

            val money: Double

            if(amount.editText!!.text.isEmpty() || categoryMenu.editText!!.text.isEmpty()){
                dismiss()
            }else if(amount.editText!!.text.toString().toDouble() == 0.0){
                amount.editText!!.text.clear()
                Toast.makeText(context, "Transaction amount cannot be 0!", Toast.LENGTH_SHORT).show()
            }else {
                money = if (radioGroup.checkedButtonId == R.id.add) {
                    amount.editText!!.text.toString().toDouble()
                } else {
                    0 - amount.editText!!.text.toString().toDouble()
                }

                activityData.insertTransaction(
                    Transaction(
                        0,
                        activityData.getEmail(),
                        money,
                        categoryMenu.editText!!.text.toString(),
                        standardDate
                    )
                )

                val lastAmount : TextView = requireActivity().findViewById(R.id.lastAmount)
                val lastDate : TextView = requireActivity().findViewById(R.id.lastDate)
                val lastImage : ImageView = requireActivity().findViewById(R.id.lastImage)
                val trans : List<Transaction> = activityData.getUserWithTransaction()
                lastAmount.text = activityData.formatMoney(trans.last().amount)
                lastDate.text = trans.last().date
                lastImage.setImageResource(activityData.getDrawable(trans.last().category))
                (parentFragment as MainFragment).updateView()
                dismiss()
            }

        }

        //cancelButton
        val cancel : Button = inflateView.findViewById(R.id.popupBack)
        cancel.setOnClickListener{
            dismiss()
        }

        return inflateView
    }

}