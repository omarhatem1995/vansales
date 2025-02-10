package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface RequestStatusRepo {
    suspend fun requestStatus(requestStatusRequestModel: RequestStatusRequestModel):
            ApiResponse<BillToBillCheckResponseModel, Error>
}