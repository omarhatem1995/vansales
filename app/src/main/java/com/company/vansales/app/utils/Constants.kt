package com.company.vansales.app.utils

object Constants {
    const val BASE_URL = "83.96.27.28"
    const val BASE_PORT = "8020"

    const val START_DAY_ACTIVITY = "START_DAY_ACTIVITY"

    const val ARABIC_LANGUAGE = "AR"
    const val ENGLISH_LANGUAGE = "EN"
    var CURRENT_LANG = "EN"

    const val CHECK_LIST = "CHECK_LIST"
    const val FOC_REASON = "FOC_REASON"
    const val FAILED_REASON = "FAILED_REASON"
    const val RET_REASON = "RET_REASON"

    const val USER_BLOCKED = "B"

    //Materials Truck Item Category
    const val MATERIAL_CATEGORY_SELLABLE = "S"
    const val MATERIAL_CATEGORY_FREE = "F"
    const val MATERIAL_CATEGORY_FREE_FOR_FOC_INVOICE = ""
    const val MATERIAL_CATEGORY_DAMAGE = "D"
    const val MATERIAL_CATEGORY_MISSING = "M"
    const val MATERIAL_CATEGORY_RETURN_SELLABLE = "RS"
    const val MATERIAL_CATEGORY_RETURN_DAMAGE = "RD"
    const val MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE = "EXR"
    const val MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE = "EXRS"
    const val MATERIAL_CATEGORY_EXCHANGE_SELLABLE = "EXS"
    const val UNIT_ALLOW_DECIMAL = "KAR"
    const val DELETED = "D"

    //Sync Status
    const val STATUS_LOADED = "Loaded"
    const val STATUS_ERROR = "Error"
    const val STATUS_EMPTY = "Empty"
    const val NUMBER_OF_RETRIES = 3L
    //Sync Types
    const val CUSTOMERS = "CUSTOMERS"
    const val MATERIALS = "MATERIALS"
    const val MATERIALS_UNIT = "MATERIALS_UNIT"
    const val TRUCK_CONTENT = "TRUCK_CONTENT"
    const val VISITS = "VISITS"
    const val PRICES = "PRICES"
    const val CONDITIONS = "CONDITIONS"


    //Visit Type
    const val HEADER_VISIT = "HEADER_VISIT"
    const val PLANNED_VISIT = "PLANNED"
    const val UNPLANNED_VISIT = "UN PLANNED"

    //Visit Status
    const val IN_PROGRESS_VISIT = "In Progress"
    const val FINISHED_VISIT = "Finished"
    const val UNFINISHED_VISIT = "Pending"
    const val FAILED_VISIT = "FAIL"

    //On Truck Materials and Batches Types
    const val MATERIAL_SELLABLE = "S"
    const val MATERIAL_DELIVERIES = "Delivery"
    const val MATERIAL_RETURN = "R"
    const val MATERIAL_DAMAGED = "D"

    //batch id
    const val BATCH_RETURN_SELLABLE = "8888888888"
    const val BATCH_RETURN_DAMAGED = "9999999999"

    //Transactions History Types
    const val TRANSACTION_ADD = "Add"
    const val TRANSACTION_SUBTRACT = "Subtract"
    const val OPEN_DAY_STOCK = "Open Stock"
    const val REQUEST_ADD = "RequestAdd"

    //Filter Bottom Sheet
    const val FROM_VISITS = "visits"
    const val FROM_CUSTOMERS = "customers"

    const val PAYMENT_TERM_CASH = "CASH"

    //Pricing
    const val scaleUnitQty = "Q"
    const val scaleUnitAmount = "A"

    //Pricing(benefit)
    const val conditionPercentage = "A"
    const val conditionFixedAmount = "B"
    const val conditionQty = "C"
    const val conditionFOC = "T"
    const val conditionMaterialGroup = "G"
    const val conditionManual = "M"
    const val conditionItemHeader = "H"
    const val conditionPercentageHeaderExculsion = "AE"

    //Taxation Types
    const val TAXATION_YVAT = "YVAT"
    const val TAXATION_YEXC = "YEXC"

    //MaterialsList Types
    const val MATERIAL_LIST_FREE_TYPE = "Free"
    const val MATERIAL_LIST_ITEM_TYPE = "Item"

    const val IS_OFFLINE = "IS_OFFLINE"
    const val IS_ONLINE = "IS_ONLINE"


}