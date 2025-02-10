package com.company.vansales.app.datamodel.models.mastermodels

class ApprovalsStatusRequestModel(
    val iData: List<DocumentStatus>
)

class DocumentStatus(
    val exdoc: String,
    val sapDoc: String
)