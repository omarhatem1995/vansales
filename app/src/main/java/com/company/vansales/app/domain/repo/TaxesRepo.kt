package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.models.mastermodels.TaxesRequestModel

interface TaxesRepo {
    suspend fun getTaxes(taxesRequest : TaxesRequestModel):
            ApiResponse<BaseResponse<Taxes>, Error>

    suspend fun upsertTaxes(taxes : List<Taxes>)
}