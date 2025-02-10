package com.company.vansales.app.activity

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.company.vansales.R
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_FREE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_SELLABLE
import com.company.vansales.app.utils.DocumentsConstants.BTC_INVOICE
import com.company.vansales.app.utils.enums.BatchScanTypesEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CashMaterialSelectionActivity : MaterialSelectionActivity() {

    override fun onStart() {
        super.onStart()
        binding.materialSelectionHeaderTextView.text = resources.getString(R.string.cash)
        if(resources.configuration.locale.toString() == "ar"){
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }
        materialsVM.isInvoice = true
        isCash = true
        materialsVM.isFOC = true
        if(materialsVM.getCustomer() != null && materialsVM.getCurrentActiveVisit() != null){
            materialsVM.currentCustomer = materialsVM.getCustomer()!!
            materialsVM.currentVisit = materialsVM.getCurrentActiveVisit()!!
        }
        else{
            this.finish()
        }

        materialsVM.routes = materialsVM.getRoute(materialsVM.currentVisit.route!!)
        materialsVM.createListFromTruckItem()
        binding.materialSelectionTotalCreditValueTextView.visibility = View.GONE
        binding.materialSelectionTotalCreditTextView.visibility = View.GONE
        binding.refreshCredit.visibility = View.GONE
        binding.payInvoiceCheckBox.visibility = View.VISIBLE
        binding.payAmount.visibility = View.VISIBLE
    }

    override fun proceed() {
        if(binding.payInvoiceCheckBox.isChecked || binding.payAmount.text.toString().isNotEmpty()) {
        val iSalesDocHeader =
            materialsVM.setISalesDocHeader(
                BTC_INVOICE,
                exInvoiceNumber)

        val headerItems = materialsVM.setAllHeaderItems(
            exInvoiceNumber,
            MATERIAL_CATEGORY_SELLABLE,
            MATERIAL_CATEGORY_FREE)

        if(materialsVM.totalFOC != 0.0 || materialsVM.listOfMaterialGroupItems.isNotEmpty()){
//            binding.separateInvoice.isChecked = true;
            if(binding.separateInvoice.isChecked)
            iSalesDocHeader.secondExDoc = materialsVM.salesDocRepository.buildExInvoiceNumberFreeNew(materialsVM.routes.route)

        }
        iSalesDocHeader.discountValue = materialsVM.totalMaterialDiscount.toBigDecimal()

        if (binding.payInvoiceCheckBox.isChecked)
            iSalesDocHeader.customizeheader = iSalesDocHeader.totalvalue.toString()
        else
            iSalesDocHeader.customizeheader = binding.payAmount.text.toString()

            if(checkPaidAmountAvailability(iSalesDocHeader.totalvalue.toDouble())) {
                /*val intent = Intent(applicationContext, BatchScanActivity::class.java)
                intent.putExtra("customerName", customerName)
                intent.putExtra("isApproval", false)
                intent.putExtra("materialsISalesDocHeader", iSalesDocHeader)
                intent.putExtra("materialsHeaderItems", headerItems)
                intent.putExtra("type", BatchScanTypesEnum.CASH_MATERIALS)
                this.startActivity(intent)*/
            }else{
//                Toast.makeText(this,getString(R.string.please_select_paid_amount),Toast.LENGTH_LONG).show()
            }
        }else{
//            Toast.makeText(this,getString(R.string.please_select_paid_amount),Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPaidAmountAvailability(totalAmount:Double): Boolean {
        return !(!binding.payInvoiceCheckBox.isChecked && binding.payAmount.text.toString().isNotEmpty() && totalAmount < binding.payAmount.text.toString().toDouble() )
    }
}