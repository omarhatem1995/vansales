package com.company.vansales.app.datamodel.models.mastermodels

import java.io.Serializable

class BillToBillCheckResponseModel (
    val type : String,
    val msg1 : String,
    val msg2 : String,
    val data : BillToBillData,
    val status : String,
    val success : String,
    ): Serializable
class BillToBillData(
    val orders : List<Orders>,
    val items : List<Items>,
    val itemBatches : List<ItemBatches>
) : Serializable
class Orders(
    val salesorg : String,
    val distchannel : String,
    val division : String,
    val plant : String,
    val location : String?,
    val route : String?,
    val driver : String?,
    val customer : String?,
    val doctype : String?,
    val exdoc : String,
    val creationdate : String,
    val creationtiem : String,
    val totalvalue : String,
    val paymentter : String,
    val docstatus : String,
    val customizeheader : String,
    val visitid : String,
    val visititem : String,
    val sapDoc : String
): Serializable

class Items (
    val exdoc : String,
    val itemno : String,
    var itemcategory : String,
    val materialno : String,
    val quantity : String,
    val uom : String,
    val totalvalue : Double,
    val currency : String,
    val customizeitem : String,
    val returnreason : String
    ): Serializable

class ItemBatches(

)