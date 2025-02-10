package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.RequestStatusRequestModel
import retrofit2.http.Body
import retrofit2.http.POST

interface RequestStatusGateway {

    @POST("Document/btb")
    suspend fun requestStatus(@Body requestStatusRequestModel: RequestStatusRequestModel):
            ApiResponse<BillToBillCheckResponseModel, Error>

}