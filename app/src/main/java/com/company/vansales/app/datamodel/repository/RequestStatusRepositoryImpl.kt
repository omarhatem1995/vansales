package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.RequestStatusRequestModel
import com.company.vansales.app.datamodel.services.api.RequestStatusGateway
import com.company.vansales.app.domain.repos.RequestStatusRepo

class RequestStatusRepositoryImpl (
    private val requestStatusGateway: RequestStatusGateway
    ) : RequestStatusRepo {

    override suspend fun requestStatus(requestStatusRequestModel: RequestStatusRequestModel):
            ApiResponse<BillToBillCheckResponseModel, Error> {
        return requestStatusGateway.requestStatus(requestStatusRequestModel)
    }

}