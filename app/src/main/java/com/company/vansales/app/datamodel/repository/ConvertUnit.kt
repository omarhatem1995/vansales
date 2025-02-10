package com.company.vansales.app.datamodel.repository

import android.app.Application
import android.util.Log
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.MaterialsUnit
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.MaterialsDAO
import com.company.vansales.app.datamodel.room.MaterialsUnitDAO
import com.company.vansales.app.datamodel.room.PricesDAO
import com.company.vansales.app.utils.AppUtils.round

class ConvertUnit(application: Application) {
    private var materialsDAO: MaterialsDAO
    private var materialUnitDAO: MaterialsUnitDAO
    private var pricesUnitDAO: PricesDAO
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    init {
        materialUnitDAO = appDB.getMaterialsUnit()
        pricesUnitDAO = appDB.getPrices()
        materialsDAO = appDB.getMaterials()
    }
    fun convertUnit(
        materialNumber: String,
        FromUnit: String,
        ToUnit: String,
        Quantity: Double
    ): Double {
        var result = convertToBase(materialNumber, FromUnit, Quantity)
        result = convertFromBase(materialNumber, ToUnit, result)
        return result
    }

    fun getMaterialBaseUnit(materialNumber: String) : String{
        val material = materialUnitDAO.getMaterialsUnitByMaterialNumber(materialNumber)[0]
//        if(material.baseUnit == "EA")
            Log.d("ThisisEachItem", "loglog ${material.baseUnit}" )

        return material.baseUnit!!
    }
    fun convertToBase(materialNumber: String, fromUnit: String, quantity: Double): Double {
        val materialUnitItem =
            materialUnitDAO.getMaterialsUnitByPrimary(materialNumber, fromUnit)[0]
        Log.d("ThisIsEachItem" , " 1111 ${materialUnitItem.materialNo} , ${materialUnitItem.baseUnit} , " +
                "${materialUnitItem.denominator} , ${materialUnitItem.numenrator} ${materialUnitItem.unit}")
        return round(((materialUnitItem.numenrator!! / materialUnitItem.denominator!!) * quantity).toBigDecimal()).toDouble()
    }

    fun convertToBaseWithoutRounding(materialNumber: String, fromUnit: String, quantity: Double): Double {
        val materialUnitItem =
            materialUnitDAO.getMaterialsUnitByPrimary(materialNumber, fromUnit)[0]
        Log.d("ThisIsEachItem" , " 00 2${materialUnitItem.materialNo} , ${materialUnitItem.baseUnit} , " +
                "${materialUnitItem.denominator} , ${materialUnitItem.numenrator} ${materialUnitItem.unit}")
        return (materialUnitItem.numenrator!! / materialUnitItem.denominator!!) * quantity
    }

    fun convertToBaseWithoutRounding(materialNumber: String, fromUnit: String, toUnit : String ,quantity: Double): Double {
        val materialUnitItem =
            materialUnitDAO.getMaterialsUnitByPrimary(materialNumber, fromUnit)[0]
        Log.d("ThisIsEachItem" , " 3 ${materialUnitItem.materialNo} , ${materialUnitItem.baseUnit} , " +
                "${materialUnitItem.denominator} , ${materialUnitItem.numenrator} ${materialUnitItem.unit}")
        var convertToBase = (materialUnitItem.numenrator!! / materialUnitItem.denominator!!) * quantity
        var convertResult = 0.0
        if(fromUnit != toUnit){
            var toUnit = materialUnitDAO.getMaterialUnitByPrimary(materialNumber , toUnit)
            convertResult = (toUnit.denominator!! / toUnit.numenrator!!) * convertToBase
        }else{
            convertResult = convertToBase
        }
        return convertResult
    }

    fun getSingleItemPrice(materialNo : String, customer : Customer?, inputUnit : String, quantity : Double) : Double {
        var price = pricesUnitDAO.getMaterialPriceByPrimary(materialNo, customer!!.customer , customer!!.distChannel)
        var material = materialsDAO.getMaterialByMaterialNo(materialNo)
        var materialPrice = 0.0
        var conversionResult = 0.0
        if(price.condUnit != inputUnit){
            conversionResult = convertToBaseWithoutRounding(materialNo,price.condUnit!!,inputUnit,1.0)
            materialPrice = getSingleUnitPrice(price)/ conversionResult
        }else {

            return getSingleUnitPrice(price)
        }
        return materialPrice
    }

    fun getSingleItemPriceNothingEquals(materialNo : String, customer : Customer?, inputUnit : String , quantity : Double) : Double {
        var price = pricesUnitDAO.getMaterialPriceByPrimary(materialNo, customer!!.customer , customer!!.distChannel)
        var material = materialsDAO.getMaterialByMaterialNo(materialNo)
        var materialPrice = 0.0
        var conversionResult = 0.0
        if(price.condUnit != inputUnit){
            if(inputUnit != material.baseUnit)
            conversionResult = convertToBase(materialNo, inputUnit, quantity = quantity)
            conversionResult = round(conversionResult)
            return getSingleItemPrice(materialNo,customer,material.baseUnit!!,conversionResult)
        }
        return materialPrice
    }

    fun getSingleUnitPrice(price : Prices) : Double{
        if(price.scaleValue != null && price.scaleValue != 0.0)
        return price.condValue!! / price.scaleValue!!
        return price.condValue!!
    }

    fun convertToBaseWithoutRoundingp(materialNumber: String, fromUnit: String, quantity: Double): Double {
        val materialUnitItem =
            materialUnitDAO.getMaterialsUnitByPrimary(materialNumber, fromUnit)[0]
        Log.d("ThisIsEachItem" , "${materialUnitItem.materialNo} , ${materialUnitItem.baseUnit} , " +
                "${materialUnitItem.denominator} , ${materialUnitItem.numenrator} ${materialUnitItem.unit}")
        return (materialUnitItem.numenrator!! / materialUnitItem.denominator!!) * 1
    }

    fun getMaterialBaseUnit(materialNumber: String, fromUnit: String): String {
        val materialUnitItem =
            materialUnitDAO.getMaterialsUnitByPrimary(materialNumber, fromUnit)[0]
        Log.d("ThisIsEachItem" , "2220 ${materialUnitItem.materialNo} , ${materialUnitItem.baseUnit} , " +
                "${materialUnitItem.denominator} , ${materialUnitItem.numenrator} ${materialUnitItem.unit}")
        return materialUnitItem.baseUnit.toString()
    }

    fun convertFromBase(materialNumber: String, toUnit: String, quantity: Double): Double {
        val materialUnitItem = materialUnitDAO.getMaterialsUnitByPrimary(materialNumber, toUnit)[0]
        return if (toUnit != materialUnitItem.baseUnit)
            (materialUnitItem.denominator!! / materialUnitItem.numenrator!!) * quantity
        else
            quantity
    }
    fun convertMaterialUnit(materialNumber: String, toUnit: String, quantity: Double): String? {
        val materialUnitItem = materialUnitDAO.getMaterialsUnitByPrimary(materialNumber, toUnit)[0]
         return  materialUnitItem.baseUnit
    }
    fun getMaterialUnits(materialNumber: String): List<MaterialsUnit> {
        return materialUnitDAO.getMaterialsUnitByMaterialNumber(materialNumber)
    }
    fun getMaterialsBarcode(materialNumber: String): ArrayList<String> {
        val materials = materialUnitDAO.getMaterialsUnitByMaterialNumber(materialNumber)
        val barcodes = ArrayList<String>()
        for (i in materials.indices) {
            barcodes.add(materials[i].barcode!!)
        }
        return barcodes
    }
}