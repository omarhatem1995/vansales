package com.company.vansales.app.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.company.vansales.R
import com.company.vansales.app.activity.MaterialSelectionActivity
import com.company.vansales.app.utils.AppUtils.round
import com.company.vansales.app.viewmodel.MaterialsViewModel
import com.company.vansales.databinding.InvoiceDetailsDialogBinding


class InvoiceDetailsDialog : DialogFragment() {

    lateinit var binding: InvoiceDetailsDialogBinding
    val materialsVM: MaterialsViewModel by lazy {
        MaterialSelectionActivity.instance?.materialsVM!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InvoiceDetailsDialogBinding.inflate(LayoutInflater.from(context),
            container, true)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        isCancelable = true
        initView()
        callBacks()
        initButtons()
    }

    private fun callBacks() {
    }

    private fun initButtons(){
        binding.invoiceDetailsDialogOkButton.setOnClickListener {
            dialog?.dismiss()
        }
        binding.invoiceDetailsDialogDetailsButton.setOnClickListener {
            dialog?.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        var total = 0.0
        var materialGroupSelectedTotal = 0.0
        materialsVM.listOfMaterialGroupItems.forEach {
            materialsVM.finishSelectedFreeList(it.key)
            total += it.value.totalPrice
            materialGroupSelectedTotal += it.value.selectedPrice
        }
        binding.invoiceDetailsDialogTotalAmountTextView.text = round(materialsVM.selectedItemTotalPrice).toString()
        binding.invoiceDetailsDialogManualPercentTextView.setText("")
        binding.invoiceDetailsDialogManualAmountTextView.text = ""
        binding.invoiceDetailsDialogTotalDiscountTextView.text = materialsVM.totalMaterialDiscount.toString()
        Log.d("materialVMTOtal" , "${materialsVM.selectedFreeTotalPrice}  , ${materialsVM.getTotalMaterialGroupPrice()} ${total}")
        binding.invoiceDetailsDialogFocTextView.text =  "${round(materialsVM.selectedFreeTotalPrice)}/${round(materialsVM.totalFOC)}"
        binding.invoiceDetailsDialogNetAmountTextView.text = "${round(materialsVM.getTotalMaterialGroupPrice())}/${round(total)}"
    }

}