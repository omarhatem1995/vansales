package com.company.vansales.app.datamodel.models.localmodels

class CustomizeCondition (
    var condition : String,
    var amount : Double,
    var scaleValue : Double,
    var scaleUnit : String,
    var condSign : String,
    var condValue : Double,
    var selectedAmount : Double,
    var selectedPrice : Double,
    var totalPrice : Double,
    var materialNo : ArrayList<String>,
    var materialByUnit : ArrayList<MaterialGroup>
)

data class MaterialGroup(
    val materialNo : String,
    val materialUnit : String,
)