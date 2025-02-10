package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface PayersRepo {
    suspend fun getPayers(payersRequestModel: PayersRequestModel):
            ApiResponse<PayersResponseModel, Error>
}