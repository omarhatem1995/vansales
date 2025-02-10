package com.company.vansales.app.utils

import android.app.Application
import android.util.Log
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.models.mastermodels.MaterialsUnit
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.MaterialsDAO
import com.company.vansales.app.datamodel.room.MaterialsUnitDAO
import com.company.vansales.app.datamodel.room.PricesDAO
import com.company.vansales.app.utils.AppUtils.round

class PricingUtils(application : Application) {

    private var materialsDAO: MaterialsDAO
    private var materialUnitDAO: MaterialsUnitDAO
    private var pricesUnitDAO: PricesDAO
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!

    init {
        materialUnitDAO = appDB.getMaterialsUnit()
        pricesUnitDAO = appDB.getPrices()
        materialsDAO = appDB.getMaterials()
    }

    fun calculatePrice(material : Materials, customer: Customer, inputUnit: String, quantity: Double) : Double{
        val unitPrice = getSinglePrice(material,customer, inputUnit,quantity)
        if(checkMaterialFromPAKToEA(material,customer,inputUnit) && (material.materialNo == "1SEP027" || material.materialNo == "1SEP028")){
            return unitPrice
        }else {
            val unitPriceFromDB = pricesUnitDAO.getMaterialPriceByPrimary(
                material.materialNo,
                customer.customer,
                customer.distChannel
            )
            if (inputUnit != unitPriceFromDB.condUnit) {
                if (inputUnit == material.baseUnit) {
                    Log.d(
                        "calculatePrice",
                        "${material.materialNo} $inputUnit == ${material.baseUnit} $unitPrice * $quantity"
                    )
                    return round((unitPrice.toBigDecimal() * quantity.toBigDecimal())).toDouble()
                } else {
                    Log.d(
                        "calculatePrice",
                        "else convert ${material.materialNo} $inputUnit == ${material.baseUnit} $unitPrice * $quantity"
                    )
                    var quantityInBaseUnit = round(
                        convertToBaseWithoutRounding(
                            material.materialNo,
                            inputUnit,
                            material.baseUnit!!,
                            quantity
                        ).toBigDecimal()
                    )
                    return calculatePrice(
                        material,
                        customer,
                        material.baseUnit!!,
                        quantityInBaseUnit.toDouble()
                    )
                }
            } else {
                Log.d(
                    "calculatePrice",
                    "else else ${material.materialNo} $inputUnit == ${material.baseUnit} $unitPrice * $quantity"
                )
                return (unitPrice.toBigDecimal() * quantity.toBigDecimal()).toDouble()
            }
        }
    }

    fun checkMaterialFromPAKToEA(material : Materials, customer : Customer, inputUnit : String):Boolean{
        val unitPrice = pricesUnitDAO.getMaterialPriceByPrimary(material.materialNo,customer.customer , customer.distChannel)
        return unitPrice.condUnit == "PAK"  && inputUnit == "EA"
    }

    private fun getSinglePrice(material : Materials, customer : Customer, inputUnit : String,quantity:Double?) : Double{
        val unitPrice = pricesUnitDAO.getMaterialPriceByPrimary(material.materialNo,customer.customer , customer.distChannel)
         if(unitPrice.condUnit != inputUnit){
            if(unitPrice.condUnit == "PAK"  && inputUnit == "EA"){
                val materialUnitItem =
                    materialUnitDAO.getMaterialsUnitByPrimary(material.materialNo, "PAK")[0]
                return handlingPAKBaseToEA(materialUnitItem,unitPrice.condValue , quantity)?:0.0
            }else{
                val convertResult = convertToBaseWithoutRounding(material.materialNo,unitPrice.condUnit!!,inputUnit,1.0)
                val priceForSingleUnit = getSingleUnitPrice(unitPrice)
                Log.d("getSinglePrice", "$convertResult $priceForSingleUnit")
                return  (priceForSingleUnit.toBigDecimal() / convertResult.toBigDecimal()).toDouble()
            }
        }else{
             return getSingleUnitPrice(unitPrice)
        }
    }

    private fun handlingPAKBaseToEA(
        materialUnitItem: MaterialsUnit,
        condValue: Double?,
        quantity: Double?
    ) : Double? {
        val x = (quantity?.div((materialUnitItem.numenrator!!)))
        val y = x?.let { round(it) }
        val z = y?.times(condValue!!)
        return z?.let { round(it) }
    }

    private fun getSingleUnitPrice(price : Prices) : Double{
        if(price.scaleValue != null && price.scaleValue != 0.0)
            return price.condValue!! / price.scaleValue!!
        return price.condValue!!
    }

    fun convertToBaseWithoutRounding(materialNumber: String, fromUnit: String, toUnit : String ,quantity: Double): Double {
        Log.d("getMaterialUnitByPrim", "$materialNumber ,$fromUnit , $toUnit , $quantity")
        val materialUnitItem =
            materialUnitDAO.getMaterialsUnitByPrimary(materialNumber, fromUnit)[0]
        val convertToBase = (materialUnitItem.numenrator!! / materialUnitItem.denominator!!) * quantity
        Log.d("getConvertToWith", "$materialNumber $fromUnit : $convertToBase $quantity : " +
                "${materialUnitItem.numenrator} ${materialUnitItem.denominator} $quantity")
        var convertResult = 0.0
        convertResult = if(fromUnit != toUnit){
            val toUnitValue = materialUnitDAO.getMaterialUnitByPrimary(materialNumber , toUnit)
            Log.d("getConvertToWith", "$materialNumber $fromUnit $toUnit, $convertToBase ${toUnitValue.denominator} , ${toUnitValue.numenrator}")
            (toUnitValue.denominator!! / toUnitValue.numenrator!!) * convertToBase
        }else{
            Log.d("getConvertToWith", "else $materialNumber $fromUnit $toUnit, ${convertToBase} ")
            convertToBase
        }
        return convertResult
    }
}
