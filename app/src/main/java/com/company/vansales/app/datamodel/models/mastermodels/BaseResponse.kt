package com.company.vansales.app.datamodel.models.mastermodels

open class BaseResponse<T>(
    var type: String,
    var msg1: String,
    var msg2: String,
    var timeStamp: String? = null,
    var data : List<T>
)
