package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface ApprovalsStatusRepo {
    suspend fun approvalsStatus(approvalsStatusRequestModel: ApprovalsStatusRequestModel):
            ApiResponse<BaseResponse<ApprovalsStatusResponseModel>, Error>
}