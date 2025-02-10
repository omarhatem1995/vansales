package com.company.vansales.app.utils

import com.company.vansales.app.datamodel.models.localmodels.Benefits
import com.company.vansales.app.datamodel.models.localmodels.CustomizeCondition
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import java.math.BigDecimal

class PricingCalculations {

    private lateinit var benefitList: ArrayList<Benefits>

    fun getBenefitList(
        price: BigDecimal,
        conditionsList: MutableList<ItemPriceCondition>,
        neededQuantity: Double
    ): ArrayList<Benefits> {
        benefitList = ArrayList()
        var verifyScale = false
        var isExist = -1
        var benefitObj: Benefits
        for (condition in conditionsList) {
            benefitObj = Benefits()
            if (condition.scaleUnit == Constants.scaleUnitAmount)
                verifyScale = scaleAmount(condition, price)
            else if (condition.scaleUnit == Constants.scaleUnitQty)
                verifyScale = scaleQuantityAmount(condition, neededQuantity)

            if (verifyScale) {
                when (condition.condUnit) {
                    Constants.conditionFixedAmount -> {
                        benefitObj.benefitValue = benefitFixedAmount(condition)
                        benefitObj.benefitType = Constants.conditionFixedAmount
                        benefitObj.condition = condition.condition
                        benefitObj.description =
                            "Fixed Amount: ${AppUtils.round(benefitObj.benefitValue)}"
                        isExist = checkBenefitExist(benefitObj)
                    }
                    Constants.conditionPercentage -> {
                        benefitObj.benefitValue = benefitPercentage(condition, price)
                        benefitObj.discountPercentage = condition.condValue!!
                        benefitObj.benefitType = Constants.conditionPercentage
                        benefitObj.condition = condition.condition
                        benefitObj.description =
                            "Percentage: ${AppUtils.round(benefitObj.discountPercentage)}%  Value: ${
                                AppUtils.round(benefitObj.benefitValue)
                            }"
                        isExist = checkBenefitExist(benefitObj)
                    }
                    Constants.conditionQty -> {
                        benefitObj.benefitValue = benefitQuantity(condition)
                        benefitObj.benefitType = Constants.conditionQty
                        benefitObj.condition = condition.condition
                        benefitObj.description =
                            "Quantity: ${AppUtils.round(benefitObj.benefitValue)}"
                        isExist = checkBenefitExist(benefitObj)
                        //TODO mange quantity benefit
                    }
                    Constants.conditionFOC -> {
                        benefitObj.benefitValue = benefitFOC(condition, price)
                        benefitObj.benefitType = Constants.conditionFOC
                        benefitObj.condition = condition.condition
                        benefitObj.description = "FOC: ${AppUtils.round(benefitObj.benefitValue)}"
                        isExist = checkBenefitExist(benefitObj)
                        //TODO mange FOC benefit
                    }
                    else -> isExist = -2
                }
                if (isExist == -1 && benefitObj.benefitValue > BigDecimal.ZERO)
                    benefitList.add(benefitObj)
                else if (isExist > -1)
                    benefitList[isExist] = benefitObj
            }
        }
        return benefitList
    }

    fun getBenefitHeader(
        price: BigDecimal,
        conditionsList: MutableList<ItemPriceCondition>,
        neededQuantity: Double
    ): ArrayList<Benefits> {
        benefitList = ArrayList()
        var verifyScale = false
        var isExist = -1
        var benefitObj: Benefits
        for (condition in conditionsList) {
            benefitObj = Benefits()
            if (condition.scaleUnit == Constants.scaleUnitAmount)
                verifyScale = scaleAmount(condition, price)
            else if (condition.scaleUnit == Constants.scaleUnitQty)
                verifyScale = scaleQuantityAmount(condition, neededQuantity)

            if (verifyScale) {
                when (condition.condUnit) {
                    Constants.conditionFixedAmount -> {
                        benefitObj.benefitValue = benefitFixedAmount(condition)
                        benefitObj.benefitType = Constants.conditionFixedAmount
                        benefitObj.condition = condition.condition
                        benefitObj.description =
                            "Fixed Amount: ${AppUtils.round(benefitObj.benefitValue)}"
                        isExist = checkBenefitExist(benefitObj)
                    }
                    Constants.conditionPercentage -> {
                        benefitObj.benefitValue = benefitPercentage(condition, price)
                        benefitObj.discountPercentage = condition.condValue!!
                        benefitObj.benefitType = Constants.conditionPercentage
                        benefitObj.condition = condition.condition
                        benefitObj.description =
                            "Percentage: ${AppUtils.round(benefitObj.discountPercentage)}%  Value: ${
                                AppUtils.round(benefitObj.benefitValue)
                            }"
                        isExist = checkBenefitExist(benefitObj)
                    }
                    Constants.conditionQty -> {
                        benefitObj.benefitValue = benefitQuantity(condition)
                        benefitObj.benefitType = Constants.conditionQty
                        benefitObj.condition = condition.condition
                        benefitObj.description =
                            "Quantity: ${AppUtils.round(benefitObj.benefitValue)}"
                        isExist = checkBenefitExist(benefitObj)
                        //TODO mange quantity benefit
                    }
                    Constants.conditionFOC -> {
                        benefitObj.benefitValue = benefitFOC(condition, price)
                        benefitObj.benefitType = Constants.conditionFOC
                        benefitObj.condition = condition.condition
                        benefitObj.description = "FOC: ${AppUtils.round(benefitObj.benefitValue)}"
                        isExist = checkBenefitExist(benefitObj)
                        //TODO mange FOC benefit
                    }
                    else -> isExist = -2
                }

                if (isExist == -1 && benefitObj.benefitValue > BigDecimal.ZERO)
                    benefitList.add(benefitObj)
                else if (isExist > -1)
                    benefitList[isExist] = benefitObj
            }
        }
        return benefitList
    }

    private fun checkBenefitExist(benefit: Benefits): Int {
        var isExist = false
        val notExist = -1
        val existButLargerValue = -2
        if (benefitList.isNotEmpty()) {
            for (i in benefitList.indices) {
                if (benefit.benefitType == benefitList[i].benefitType &&
                    benefit.condition == benefitList[i].condition
                ) {
                    isExist = true
                    if (benefit.benefitValue > benefitList[i].benefitValue)
                        return i
                }
            }
        }
        if (isExist)
            return existButLargerValue
        return notExist
    }

    private fun benefitPercentage(condition: ItemPriceCondition, price: BigDecimal): BigDecimal {
        return price * (condition.condValue!! / 100).toBigDecimal()
    }

    private fun benefitFixedAmount(condition: ItemPriceCondition): BigDecimal {
        return condition.condValue!!.toBigDecimal()
    }

    fun benefitQuantity(condition: ItemPriceCondition): BigDecimal {
        return condition.condValue!!.toBigDecimal()
    }

    private fun benefitFOC(condition: ItemPriceCondition, price: BigDecimal): BigDecimal {
        return price * (condition.condValue!! / 100).toBigDecimal()
    }

    fun calculateFOCValue(value: Double, price: BigDecimal): BigDecimal {
        return price * (value / 100).toBigDecimal()
    }


    fun calculatePercentageValue(value: Double, price: BigDecimal): BigDecimal {
        return price * (value / 100).toBigDecimal()
    }

    fun scaleAmount(condition: ItemPriceCondition, price: BigDecimal): Boolean {
        return price >= condition.scaleValue!!.toBigDecimal()
    }
    fun scaleAmount(condition: CustomizeCondition, price: BigDecimal): Boolean {
        return price >= condition.scaleValue.toBigDecimal()
    }

    fun scaleQuantityAmount(
        condition: ItemPriceCondition,
        neededQuantity: Double
    ): Boolean {
        return neededQuantity >= condition.scaleValue!!
    }
    fun scaleQuantityAmount(
        condition: CustomizeCondition,
        neededQuantity: Double
    ): Boolean {
        return neededQuantity >= condition.scaleValue
    }

}