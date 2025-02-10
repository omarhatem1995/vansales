package com.company.vansales.app.datamodel.models.mastermodels

class EndOfDayResponseModel (
    val data : List<DataEndOfDay>,
    val type : String,
    val msg1 : String,
    val msg2 : String,
    val status : String,
    val success : Boolean
)
class DataEndOfDay(
    val message1 : String
)