package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.models.mastermodels.TaxesRequestModel
import retrofit2.http.Body
import retrofit2.http.POST

interface TaxesGateway {

    @POST("Prices/tax")
    suspend fun getTaxes(@Body taxes: TaxesRequestModel):
            ApiResponse<BaseResponse<Taxes>, Error>

}