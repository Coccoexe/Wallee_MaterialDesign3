package com.example.md3.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.view.ActionMode
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.md3.activity.MainActivity
import com.example.md3.R
import com.example.md3.adapter.TransactionAdapter
import com.example.md3.data.entity.Transaction
import com.example.md3.utility.IActivityData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


class TransactionFragment : Fragment(){

    private lateinit var activityData : IActivityData
    private var transactionList : List<Transaction>? = null
    private lateinit var gridLayoutManager: GridLayoutManager
    private val format : SimpleDateFormat = SimpleDateFormat("EE d MMM yyyy",Locale.getDefault())
    private var filterToggle : Boolean = false
    private var color: Int = -1

    //view
    private lateinit var topAppBar : MaterialToolbar
    private lateinit var filterBar : ConstraintLayout
    private lateinit var dataList : RecyclerView
    private lateinit var noTransaction : TextView
    private lateinit var filterAmountText : TextView
    private lateinit var filterDateText : TextView
    private lateinit var filterCategoryText: TextView
    private lateinit var divider : MaterialDivider
    private lateinit var dividerFilter : MaterialDivider
    private lateinit var transactionGroup : MaterialButtonToggleGroup
    private lateinit var dateGroup: MaterialButtonToggleGroup
    private lateinit var categoryGroup: RadioGroup
    private lateinit var categoryMenu: TextInputLayout
    private lateinit var all : MaterialButton
    private lateinit var positive : MaterialButton
    private lateinit var negative : MaterialButton

    //contextBar
    private var actionMode: ActionMode? = null

    //amount -> "all", "positive", "negative"
    private lateinit var filterAmount : String
    //category -> list contain category filter
    private var filterCategory : String? = null
    //date -> date in string with format
    private var filterDate : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflateView = inflater.inflate(R.layout.fragment_transaction, container, false)
        DynamicColors.applyToActivityIfAvailable(requireActivity())

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        //filter View
        topAppBar = inflateView.findViewById(R.id.topAppBar)
        filterBar = inflateView.findViewById(R.id.filterBar)
        divider = inflateView.findViewById(R.id.divider)
        dividerFilter = inflateView.findViewById(R.id.dividerFilter)
        filterAmountText = inflateView.findViewById(R.id.transactionFilterText)
        filterDateText = inflateView.findViewById(R.id.dateFilterText)
        filterCategoryText = inflateView.findViewById(R.id.categoryFilterText)
        filterToggle = false
        //amount
        transactionGroup = inflateView.findViewById(R.id.selectTransaction)
        all = inflateView.findViewById(R.id.all_transaction)
        positive = inflateView.findViewById(R.id.positive_transaction)
        negative = inflateView.findViewById(R.id.negative_transaction)
        color = MaterialColors.getColor(inflateView,com.google.android.material.R.attr.colorOnSecondaryContainer)
        //date
        dateGroup = inflateView.findViewById(R.id.selectDate)
        //category
        categoryGroup = inflateView.findViewById(R.id.selectCategory)
        categoryMenu = inflateView.findViewById(R.id.categoryMenu)


        //default -------------------------------------------------------

        //filterBar
        filterBar.visibility = View.GONE

            //divider
        divider.visibility = View.GONE
        dividerFilter.visibility = View.GONE

            //amount
        filterAmount = "all"
        filterAmountText.visibility = View.GONE
        transactionGroup.visibility = View.GONE
        transactionGroup.check(R.id.all_transaction)
        all.iconTint = null
        all.setIconResource(R.drawable.money_in_out_color)

            //date
        //filterDate is null
        filterDateText.visibility = View.GONE
        dateGroup.visibility = View.GONE
        dateGroup.check(R.id.all_time)

            //category
        //filterCategory is empty
        filterCategoryText.visibility = View.GONE
        categoryGroup.visibility = View.GONE
        categoryGroup.check(R.id.all)
        categoryMenu.visibility = View.GONE

            //recyclerview
        dataList = inflateView.findViewById(R.id.dataList)
            //no view available
        noTransaction = inflateView.findViewById(R.id.no_transaction)
        noTransaction.visibility = View.INVISIBLE
            //gridLayout
        gridLayoutManager = GridLayoutManager(context,1,GridLayoutManager.VERTICAL,false)
            //adapter
        getTransactionList()
        setAdapter()


        // ---------------------------------------------------------------

        //listener
        //apre e chiude il menu filtri
        topAppBar.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId){
                R.id.filterMenu -> {
                    if (filterToggle)
                    {
                        topAppBar.menu.getItem(0).setIcon(R.drawable.ic_filter_down_24)
                        filterBar.visibility = View.GONE
                        divider.visibility = View.GONE
                        dividerFilter.visibility = View.GONE
                        filterAmountText.visibility = View.GONE
                        transactionGroup.visibility = View.GONE
                        filterDateText.visibility = View.GONE
                        dateGroup.visibility = View.GONE
                        filterCategoryText.visibility = View.GONE
                        categoryGroup.visibility = View.GONE
                        categoryMenu.visibility = View.GONE

                        filterToggle = false
                    }
                    else{
                        topAppBar.menu.getItem(0).setIcon(R.drawable.ic_filter_up_24)
                        filterBar.visibility = View.VISIBLE
                        divider.visibility = View.VISIBLE
                        dividerFilter.visibility = View.VISIBLE
                        filterAmountText.visibility = View.VISIBLE
                        transactionGroup.visibility = View.VISIBLE
                        filterDateText.visibility = View.VISIBLE
                        dateGroup.visibility = View.VISIBLE
                        filterCategoryText.visibility = View.VISIBLE
                        categoryGroup.visibility = View.VISIBLE
                        if (categoryGroup.checkedRadioButtonId == R.id.custom)
                        {
                            categoryMenu.visibility = View.VISIBLE
                        }
                        else{
                            categoryMenu.visibility = View.INVISIBLE
                        }
                        filterToggle = true
                    }
                    true
                }
                else -> false
            }
        }

        //applica il filtro tipo di transazione
        transactionGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked){

                val items = ArrayList<String>()
                (categoryMenu.editText as? AutoCompleteTextView)?.text?.clear()

                when(checkedId){
                    R.id.all_transaction -> {
                        filterAmount = "all"

                        all.iconTint = null
                        all.setIconResource(R.drawable.money_in_out_color)

                        positive.iconTint = ColorStateList.valueOf(color)
                        positive.setIconResource(R.drawable.money_in)

                        negative.iconTint = ColorStateList.valueOf(color)
                        negative.setIconResource(R.drawable.money_out)

                        if (categoryGroup.checkedRadioButtonId == R.id.custom) {
                            items.addAll(resources.getStringArray(R.array.income))
                            items.addAll(resources.getStringArray(R.array.expenses))
                        }

                    }
                    R.id.positive_transaction -> {
                        filterAmount = "positive"

                        all.iconTint = ColorStateList.valueOf(color)
                        all.setIconResource(R.drawable.money_in_out)

                        positive.iconTint = null
                        positive.setIconResource(R.drawable.money_in_color)

                        negative.iconTint = ColorStateList.valueOf(color)
                        negative.setIconResource(R.drawable.money_out)

                        if (categoryGroup.checkedRadioButtonId == R.id.custom) {
                            items.addAll(resources.getStringArray(R.array.income))
                        }
                    }
                    R.id.negative_transaction -> {
                        filterAmount = "negative"

                        all.iconTint = ColorStateList.valueOf(color)
                        all.setIconResource(R.drawable.money_in_out)

                        positive.iconTint = ColorStateList.valueOf(color)
                        positive.setIconResource(R.drawable.money_in)

                        negative.iconTint = null
                        negative.setIconResource(R.drawable.money_out_color)

                        if (categoryGroup.checkedRadioButtonId == R.id.custom) {
                            items.addAll(resources.getStringArray(R.array.expenses))
                        }
                    }
                }
                if (categoryGroup.checkedRadioButtonId == R.id.custom) {
                    val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                    (categoryMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                    filterCategory = categoryMenu.editText?.text.toString()
                    if (filterToggle) {
                        categoryMenu.visibility = View.VISIBLE
                    }
                    else{
                        categoryMenu.visibility = View.GONE
                    }
                }
            }
            getTransactionList()
            setAdapter()
        }

        //applica il filtro data
        dateGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked){
                when (checkedId) {
                    R.id.all_time -> {
                        filterDate = null
                    }
                    R.id.last_year -> {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.YEAR, -1)
                        filterDate = format.format(calendar.time)
                    }
                    R.id.last_three_month -> {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.MONTH, -3)
                        filterDate = format.format(calendar.time)
                    }
                    R.id.last_month -> {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.add(Calendar.MONTH, -1)
                        filterDate = format.format(calendar.time)
                    }
                }
            }
            getTransactionList()
            setAdapter()
        }

        //applica il filtro categoria
        categoryGroup.setOnCheckedChangeListener{ _, optionId ->
            run {
                when (optionId) {
                    R.id.all -> {
                        filterCategory = null

                        categoryMenu.visibility = View.INVISIBLE
                    }
                    R.id.custom -> {

                        val items = ArrayList<String>()

                        when(filterAmount){
                            "all" -> {
                                items.addAll(resources.getStringArray(R.array.income))
                                items.addAll(resources.getStringArray(R.array.expenses))

                            }
                            "positive" -> {
                                items.addAll(resources.getStringArray(R.array.income))
                            }
                            "negative" -> {
                                items.addAll(resources.getStringArray(R.array.expenses))
                            }
                        }
                        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                        (categoryMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                        filterCategory = categoryMenu.editText?.text.toString()
                        if (filterToggle) {
                            categoryMenu.visibility = View.VISIBLE
                        }
                        else{
                            categoryMenu.visibility = View.GONE
                        }
                    }
                }
            }
            getTransactionList()
            setAdapter()
        }

        (categoryMenu.editText as? AutoCompleteTextView)?.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                filterCategory = categoryMenu.editText?.text.toString()
                getTransactionList()
                setAdapter()
            }


        return inflateView
    }

    fun openContextBar(){
        actionMode = (activity as MainActivity?)!!.startSupportActionMode(mActionModeCallback)

        if (filterToggle) {
            topAppBar.menu.getItem(0).setIcon(R.drawable.ic_filter_down_24)
            filterBar.visibility = View.GONE
            divider.visibility = View.GONE
            dividerFilter.visibility = View.GONE
            filterAmountText.visibility = View.GONE
            transactionGroup.visibility = View.GONE
            filterDateText.visibility = View.GONE
            dateGroup.visibility = View.GONE
            filterCategoryText.visibility = View.GONE
            categoryGroup.visibility = View.GONE
            categoryMenu.visibility = View.GONE
            filterToggle = false
        }

    }

    fun closeContextBar() {
        actionMode?.finish()
    }

    fun updateContextBarTitle(n : Int){
        actionMode?.title = resources.getString(R.string.n_item_selected).format(n)
        if (n == transactionList!!.size) {
            actionMode?.menu?.getItem(0)?.setIcon(R.drawable.ic_check_all_24)
        } else {
            actionMode?.menu?.getItem(0)?.setIcon(R.drawable.ic_check_24)
        }
    }

    fun getFormattedMoney(num : Double) : String{
        return activityData.formatMoney(num)
    }

    fun getDrawable(category : String) : Int{
        return activityData.getDrawable(category)
    }

    private val mActionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
            mode.menuInflater.inflate(R.menu.context_transaction_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.selectAll -> {
                    (dataList.adapter as TransactionAdapter).selectionAll()
                    true
                }
                R.id.deleteSelected -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(resources.getString(R.string.delete_selected))
                        .setMessage(resources.getString(R.string.n_selected_deleted).format((dataList.adapter as TransactionAdapter).selected.size))
                        .setNeutralButton(resources.getString(R.string.cancel)){ _, _ ->
                            mode.finish()
                        }
                        .setPositiveButton(resources.getString(R.string.accept)){ _, _ ->
                            activityData.removeSelectedTransaction((dataList.adapter as TransactionAdapter).selected)
                            activityData.checkCompletedGoal()
                            activityData.updateBadge()
                            mode.finish()
                        }
                        .show()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            (dataList.adapter as TransactionAdapter).selected.clear()
            (dataList.adapter as TransactionAdapter).selectionMode = false
            getTransactionList()
            setAdapter()
        }
    }

    private fun setAdapter() {
        if (!transactionList.isNullOrEmpty()) {
            noTransaction.visibility = View.INVISIBLE
            val adapter = TransactionAdapter(context, transactionList!!, this)
            dataList.layoutManager = gridLayoutManager
            dataList.adapter = adapter
        }
        else{
            noTransaction.visibility = View.VISIBLE
            dataList.adapter = null
        }
    }

    private fun getTransactionList(){

        transactionList = activityData.getUserWithTransactionFiltered(filterAmount,filterCategory,filterDate)
    }

    override fun onPause() {
        super.onPause()
        actionMode?.finish()
    }

}