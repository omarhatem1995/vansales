package com.company.vansales.app.datamodel.models.localmodels.salesdocmodels

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class ISalesDocHeader(
    var salesorg: String,
    var distchannel: String,
    var division: String,
    var plant: String?,
    var location: String?,
    var route: String,
    var driver: String?,
    var customer: String?,
    var doctype: String,
    var exdoc: String,
    var creationdate: Date?,
    var creationtiem: Date?,
    var totalvalue: BigDecimal,
    var discountValue: BigDecimal,
    var paymentterm: String,
    var docstatus: String,
    var customizeheader: String,
    var visitid: String,
    var sapDoc: String?,
    var visititem: String?,
    var secondExDoc : String? = null,
    var returnReason : String? = null,
    var latitude : String? = null,
    var longitude : String? = null,
):Serializable