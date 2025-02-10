package com.company.vansales.app.datamodel.models.mastermodels

class PayersResponseModel (
    val type : String,
    val msg1 : String,
    val msg2 : String,
    val data : MutableList<DataPayers>,
    val status : String,
    val success : String)


class DataPayers(
    val salesOrg : String,
    val distChannel : String,
    val customer : String,
    val address : String,
    val region : String,
    val currency : String,
    val name1 : String,
    val payer : String?,
    val printOption : String,
    val paymentTerm : String,
    val credit : Double,
    val mobile : String,
    val regionArabic : String,
    val addressArabic : String,
    val telephone : String,
    val city : String,
    val longitude : Double,
    val latitude : Double,
    val customizeField : String,
)
