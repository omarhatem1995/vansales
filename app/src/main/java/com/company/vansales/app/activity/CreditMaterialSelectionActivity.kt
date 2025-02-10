package com.company.vansales.app.activity

import android.content.Intent
import android.util.Log
import android.view.View
import com.company.vansales.R
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_FREE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_SELLABLE
import com.company.vansales.app.utils.DocumentsConstants.CREDIT_INVOICE
import com.company.vansales.app.utils.enums.BatchScanTypesEnum
import com.company.vansales.app.utils.enums.TypeVisitEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreditMaterialSelectionActivity : MaterialSelectionActivity() {

    override fun onStart() {
        super.onStart()
        binding.materialSelectionHeaderTextView.text = resources.getString(R.string.credit)
        if(resources.configuration.locale.toString() == "ar"){
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }
        materialsVM.isInvoice = true
        materialsVM.isCredit = true
        materialsVM.isFOC = true
        binding.type = BatchScanTypesEnum.CREDIT_MATERIAL
        if(materialsVM.getCustomer() != null&& materialsVM.getCurrentActiveVisit() != null){
            materialsVM.currentCustomer = materialsVM.getCustomer()!!
            materialsVM.currentVisit = materialsVM.getCurrentActiveVisit()!!
        }
        else{
            this.finish()
        }
        binding.payerSpinner.visibility = View.GONE

        materialsVM.routes = materialsVM.getRoute(materialsVM.currentVisit.route!!)
        materialsVM.creditLimit = materialsVM.currentCustomer?.credit!!.toBigDecimal()
        materialsVM.createListFromTruckItem()
        binding.materialSelectionTotalCreditValueTextView.text = materialsVM.creditLimit.toString()
        refreshCredit()
    }

    override fun proceed() {
        val iSalesDocHeader =
            materialsVM.setISalesDocHeader(
                CREDIT_INVOICE,
                exInvoiceNumber
            )

        val headerItems = materialsVM.setAllHeaderItems(
            exInvoiceNumber,
            MATERIAL_CATEGORY_SELLABLE,
            MATERIAL_CATEGORY_FREE
        )

        if(materialsVM.selectedFreeMaterialsList.isEmpty()){
            binding.separateInvoice.isChecked = false
        }

        val intent = Intent(applicationContext, BatchScanActivity::class.java)
        if (typeVisit == TypeVisitEnum.APPROVAL) {
            iSalesDocHeader.paymentterm = TypeVisitEnum.APPROVAL.value
            intent.putExtra("isApproval", true)
        }else{
            intent.putExtra("isApproval", false)
        }

        if(binding.separateInvoice.isChecked && (materialsVM.totalFOC != 0.0 || materialsVM.listOfMaterialGroupItems.isNotEmpty()) ){
            iSalesDocHeader.secondExDoc = materialsVM.salesDocRepository.buildExInvoiceNumberFreeNew(materialsVM.routes.route)
            Log.d("getSecondInvoice", iSalesDocHeader.secondExDoc.toString())
        }
        Log.d("getSelectedPayer" , ": $selectedPayer")
        iSalesDocHeader.discountValue = materialsVM.totalMaterialDiscount.toBigDecimal()
        intent.putExtra("customerName", customerName)
        intent.putExtra("payer", selectedPayerID)
        intent.putExtra("payerName", selectedPayer)
        intent.putExtra("materialsISalesDocHeader", iSalesDocHeader)
        intent.putExtra("materialsHeaderItems", headerItems)
        intent.putExtra("type",BatchScanTypesEnum.CREDIT_MATERIAL)
        this.startActivity(intent)
    }
}
