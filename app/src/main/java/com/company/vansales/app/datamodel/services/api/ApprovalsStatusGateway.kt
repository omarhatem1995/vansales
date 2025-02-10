package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApprovalsStatusGateway {

    @POST("Document/approvals")
    suspend fun approvalsStatus(@Body approvalsStatusRequestModel: ApprovalsStatusRequestModel):
            ApiResponse<BaseResponse<ApprovalsStatusResponseModel>, Error>

}