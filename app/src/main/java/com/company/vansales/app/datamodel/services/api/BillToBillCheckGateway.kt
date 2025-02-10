package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface BillToBillCheckGateway {

    @POST("Document/btb")
    suspend fun billToBillCheck(@Body billToBillCheckRequestModel: BillToBillCheckRequestModel):
            ApiResponse<BillToBillCheckResponseModel, Error>

}