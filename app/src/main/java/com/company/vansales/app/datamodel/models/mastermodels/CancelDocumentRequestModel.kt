package com.company.vansales.app.datamodel.models.mastermodels

import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader

class CancelDocumentRequestModel (
    var iHeader: ISalesDocHeaderCancel,
    )

class ISalesDocHeaderCancel(
    val doctype : String,
    val sapDoc : String
)