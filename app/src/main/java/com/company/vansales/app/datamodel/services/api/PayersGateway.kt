package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.PayersRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PayersResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface PayersGateway {

    @POST("Customers/getPayers")
    suspend fun getPayers(@Body payersRequestModel: PayersRequestModel):
            ApiResponse<PayersResponseModel, Error>

}