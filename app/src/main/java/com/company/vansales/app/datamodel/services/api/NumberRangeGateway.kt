package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface NumberRangeGateway {

    @POST("config/numberRange")
    suspend fun numberRange(@Body numberRangeRequestModel: NumberRangeRequestModel):
            ApiResponse<BaseResponse<NumberRangeResponseModel>, Error>

}