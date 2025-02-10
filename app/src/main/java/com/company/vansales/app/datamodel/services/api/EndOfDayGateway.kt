package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface EndOfDayGateway {

    @POST("eod/")
    suspend fun endOfDay(@Body endOfDayRequestModel: EndOfDayRequestModel):
            ApiResponse<EndOfDayResponseModel, Error>

}