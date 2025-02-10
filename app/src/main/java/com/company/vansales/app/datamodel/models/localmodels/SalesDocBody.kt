package com.company.vansales.app.datamodel.models.localmodels.salesdocmodels

data class SalesDocBody(
    var iHeader: ISalesDocHeader,
    var itSDItems: List<ExternalHeaderItems>?,
    var itSDBatch: List<ExternalHeaderBatches>?
)