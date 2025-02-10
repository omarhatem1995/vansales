package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.services.api.ApprovalsStatusGateway
import com.company.vansales.app.domain.repos.ApprovalsStatusRepo

class ApprovalsStatusRepositoryImpl(
    private val approvalsStatusGateway: ApprovalsStatusGateway
) : ApprovalsStatusRepo {

    override suspend fun approvalsStatus(approvalsStatusRequestModel: ApprovalsStatusRequestModel):
            ApiResponse<BaseResponse<ApprovalsStatusResponseModel>, Error> {
        return approvalsStatusGateway.approvalsStatus(approvalsStatusRequestModel)
    }

}
