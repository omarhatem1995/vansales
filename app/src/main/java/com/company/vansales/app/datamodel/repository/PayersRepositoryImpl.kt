package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.PayersRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PayersResponseModel
import com.company.vansales.app.datamodel.services.api.PayersGateway
import com.company.vansales.app.domain.repos.PayersRepo

class PayersRepositoryImpl (
    private val payersGateway: PayersGateway
    ) : PayersRepo {

    override suspend fun getPayers(payersRequestModel: PayersRequestModel):
            ApiResponse<PayersResponseModel, Error> {
        return payersGateway.getPayers(payersRequestModel)
    }

}