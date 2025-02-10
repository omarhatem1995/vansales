package com.company.vansales.app.utils

import android.app.Application
import android.util.Log
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.CustomersDAO
import com.company.vansales.app.datamodel.room.MaterialsDAO
import com.company.vansales.app.datamodel.room.MaterialsUnitDAO
import com.company.vansales.app.datamodel.room.PricesDAO
import com.company.vansales.app.datamodel.room.TaxesDAO
import com.company.vansales.app.utils.Constants.TAXATION_YEXC
import com.company.vansales.app.utils.Constants.TAXATION_YVAT
import java.math.BigDecimal

class TaxationUtils(application : Application){
    private var materialsDAO: MaterialsDAO
    private var materialUnitDAO: MaterialsUnitDAO
    private var pricesUnitDAO: PricesDAO
    private var taxesDAO: TaxesDAO
    private var customersDAO: CustomersDAO
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    private var pricingCalculations = PricingCalculations()
    private var yVatValue = 0.0
    private var yExcValue = 0.0

    init {
        materialUnitDAO = appDB.getMaterialsUnit()
        pricesUnitDAO = appDB.getPrices()
        materialsDAO = appDB.getMaterials()
        customersDAO = appDB.getCustomers()
        taxesDAO = appDB.getTaxes()
    }


    fun getTaxes(customer : Customer, materialNo : String, price : BigDecimal) : Pair<BigDecimal, BigDecimal> {
        val materialTaxes = getTaxesByMaterialNo(materialNo,customer.distChannel)
        val customerTaxes = getCustomerData(customer.customer)

        if(customerTaxes.yexcTax == null || customerTaxes.yexcTax!!.isEmpty())
            customerTaxes.yexcTax = "0"
        if(customerTaxes.yvatTax == null || customerTaxes.yvatTax!!.isEmpty())
            customerTaxes.yvatTax = "0"
        if(materialTaxes.yvatTax == null || materialTaxes.yvatTax!!.isEmpty())
            materialTaxes.yvatTax = "0"
        if(materialTaxes.yexcTax == null || materialTaxes.yexcTax!!.isEmpty())
            materialTaxes.yexcTax = "0"

        val yVat = getTaxesToCustomerNoAndMaterialNo(customerTaxes.yvatTax?:"0"
            ,materialTaxes.yvatTax?:"0"
            ,TAXATION_YVAT)
        val yExc = getTaxesToCustomerNoAndMaterialNo(customerTaxes.yexcTax?:"0"
            ,materialTaxes.yexcTax?:"0"
            ,TAXATION_YEXC)

        if(yExc != null) {
            if (yExc.condUnit == Constants.conditionPercentage) {
                yExcValue = pricingCalculations.calculatePercentageValue(
                    yExc.condValue?.toDouble()!!,
                    price
                ).toDouble()
                Log.d("getValueExc", "$yExcValue , ${price}")
            }
        }
        if(yVat != null) {
            if (yVat.condUnit == Constants.conditionPercentage) {
                yVatValue = pricingCalculations.calculatePercentageValue(
                    yVat.condValue?.toDouble()!!,
                    price
                ).toDouble()
                Log.d("getValueVat", "$yVatValue , ${price}")
            }
        }
        return Pair(yVatValue.toBigDecimal(), yExcValue.toBigDecimal())
    }

    fun getTaxesByMaterialNo( materialNo: String?, distChannel : String?) : Materials {
        return materialsDAO.getTaxesForMaterial(materialNo?:"", distChannel = distChannel?:"")
    }

    fun getTaxesToCustomerAndMaterialNo(customerCode: String?, materialCode: String? , condition : String?) : Taxes {
        return taxesDAO.getTaxesForCustomerAndMaterial(customerCode?:"",
            materialCode?:"",condition?:"")
    }

    fun getCustomerData(customerNo:String): Customer {
        return customersDAO.getCurrentCustomer(customerNo)
    }
    fun getTaxesToCustomerNoAndMaterialNo(customerCode : String, materialCode : String
                                          ,condition : String) : Taxes{
        Log.d("getMaterialTaxes", "$customerCode, $materialCode, $condition")
        return taxesDAO.getTaxesForCustomerAndMaterial(customerCode = customerCode
            , materialCode = materialCode,condition = condition)
    }

}
