package com.company.vansales.app.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.enums.BatchScanTypesEnum
import com.company.vansales.app.viewmodel.BatchScanViewModel
import com.company.vansales.databinding.BatchItemBinding


class BatchListAdapter(
    private val batchList: List<HeaderBatches>,
    private var batchScanViewModel: BatchScanViewModel,
    private var quantity: String,
    private val focused: Boolean,
    private val batchScanEnum: BatchScanTypesEnum
) :
    RecyclerView.Adapter<BatchListAdapter.ViewHolder>() {
    lateinit var context: Context
    var updateUI: ((String) -> Unit)? = null
    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: BatchItemBinding =
            DataBindingUtil.inflate(
                this.layoutInflater!!,
                R.layout.batch_item, parent, false
            )
        return ViewHolder(
            binding
        )
    }

    @SuppressLint("StringFormatMatches", "StringFormatInvalid")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val batch = batchList[position]
        holder.binding.type = batchScanEnum
        handleClosingKeyboard(holder)
        holder.binding.batchExpiryDateTV.text = AppUtils.formatDate(batch.expiryDate!! )
        holder.binding.batchItemSpinner.text = batch.uom
        holder.binding.batchItemBatchID.text = batch.batch
        if (focused)holder.binding.batchItemSpinnerCountValue.requestFocus()
        holder.binding.batchTotalRequestTV.text =  context.resources.getString(
            R.string.out_of,
            AppUtils.round(batch.requestedQuantity!!)
        )
        if (batch.countedQuantity!! > 0.0) {
            holder.binding.batchItemSpinnerCountValue.setText(batch.countedQuantity.toString())
        } else {
            holder.binding.batchItemSpinnerCountValue.setText("")
        }
        if (batch.countedQuantity!! > 0.0) {
            holder.binding.batchAmountDeleteBTN.visibility = View.VISIBLE
        }
        holder.binding.batchItemSpinnerCountValue.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                handleAddAmount(holder, batch)
                true
            } else false
        }
        holder.binding.batchItemSpinnerCountValue.setOnClickListener { showKeyboard(holder) }
        holder.itemView.setOnClickListener {
            if (holder.binding.batchItemSpinnerCountValue.isFocusable)
                holder.binding.batchItemSpinnerCountValue.clearFocus()
            else
                holder.binding.batchItemSpinnerCountValue.requestFocus()
        }
        holder.binding.batchItemSpinnerCountValue.setOnFocusChangeListener { _, b ->
            if (!b) handleAddAmount(holder, batch)
        }
        holder.binding.etBatchTyping.setOnFocusChangeListener { _, b ->
            if (!b) {
                val text = holder.binding.etBatchTyping.text.toString()
                val formattedText = if (text.startsWith(".")) "0$text" else text
                batch.customizebatch = formattedText
                batch.batch = formattedText
                batchScanViewModel.addAmountInBatchAndItemStockCount(
                    quantity.toDouble(),
                    formattedText,
                    batch.itemno!!,
                    batch.materialno!!
                )
                updateUI?.invoke("")
            }
        }
        /*holder.binding.etBatchTyping.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // not used
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // not used
            }

            override fun afterTextChanged(s: Editable) {
                val input: String = s.toString()
                if (input.length > 0 && input[0] == '.') {
                    holder.binding.etBatchTyping.setText("0$input")
                    holder.binding.etBatchTyping.getText()
                        ?.let { holder.binding.etBatchTyping.setSelection(it.length) }
                }
            }
        })*/
        holder.binding.batchAmountDeleteBTN.setOnClickListener {
            handleDeleteAmount(batch)
        }

        holder.binding.batchItemSpinner.setOnClickListener {
            initUnitsMenu(batch, holder)
        }
    }

    private fun handleDeleteAmount(batch: HeaderBatches) {
        batchScanViewModel.deleteAmountInBatchAndItem(batch.batch!!, batch.itemno!!, batch.bType)
        updateUI?.invoke("")
    }

    private fun handleAddAmount(holder: ViewHolder, batch: HeaderBatches) {
        val inputText = holder.binding.batchItemSpinnerCountValue.text.toString().trim()
        val formattedText = if (inputText.startsWith(".")) "0$inputText" else inputText

        if (formattedText.isEmpty() || formattedText == ".") {
            holder.binding.batchItemSpinnerCountValue.setText("")
        } else if (formattedText.toDouble() == 0.0 ||
            !AppUtils.decimalNumberRegexChecker(formattedText)
        ) {
            holder.binding.batchItemSpinnerCountValue.setText("")
            AppUtils.showMessage(
                context,
                context.resources.getString(R.string.invalid_value_warning)
            )
        } else if (batchScanViewModel.convertUnit.convertUnit(
                batch.materialno!!,
                batch.uom!!,
                batch.baseUnit!!,
                formattedText.toDouble()
            ) > batchScanViewModel.convertUnit.convertUnit(
                batch.materialno!!,
                batch.uom!!,
                batch.baseUnit!!,
                batch.parentBaseRequestedQuantity!!
            )
        ) {
            AppUtils.showMessage(
                context,
                context.resources.getString(R.string.exceeding_batches)
            )
        } else {
            batchScanViewModel.addAmountInBatchAndItem(
                formattedText.toDouble(),
                batch.batch!!,
                batch.itemno!!,
                batch.materialno!!,
                batch.baseUnit!!
            )
            updateUI?.invoke("")
        }
    }

    private fun initUnitsMenu(batch: HeaderBatches, holder: ViewHolder) {
        val unitsPopupMenu = PopupMenu(this.context, holder.binding.batchItemSpinner)
        val units = batchScanViewModel.getMaterialsUnit(batch.materialno!!)
        for (i in units.indices) {
            unitsPopupMenu.menu.add(Menu.NONE, i, Menu.NONE, units[i])
        }
        unitsPopupMenu.show()
        unitsPopupMenu.setOnMenuItemClickListener { item ->

            val selectedUnit = item.title.toString()
            batchScanViewModel.onUnitChanged(
                batchNo = batch.batch,
                itemNo = batch.itemno,
                materialNo = batch.materialno,
                baseUnit = batch.baseUnit,
                baseCounted = batch.baseCounted,
                batchSelectedUnit = selectedUnit,
                batchQuantity = batch.baseRequestedQuantity

            )
            updateUI?.invoke("")

            true
        }
    }


    private fun handleClosingKeyboard(holder: ViewHolder) {
        batchScanViewModel.closeKeyboard.observe(context as LifecycleOwner, Observer {
            closeKeyboard(holder)
        })
    }

    private fun closeKeyboard(holder: ViewHolder) {
        val inputMethodManager =
            this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(holder.binding.batchItemSpinnerCountValue.windowToken, 0)
    }


    private fun showKeyboard(holder: ViewHolder) {
        holder.binding.batchItemSpinnerCountValue.requestFocus()
        holder.binding.batchItemSpinnerCountValue.selectAll()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return batchList.size
    }

    class ViewHolder(itemBinding: BatchItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: BatchItemBinding = itemBinding
    }

}