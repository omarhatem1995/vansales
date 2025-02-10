package com.company.vansales.app.datamodel.models.mastermodels

data class BaseBody(
    var salesOrg: String,
    var distChannel: String,
    var customer: String,
    var driver: String,
    var cdate:String ?= null,
    var tim:String ?= null,
    var timestamp:String ?= null
)