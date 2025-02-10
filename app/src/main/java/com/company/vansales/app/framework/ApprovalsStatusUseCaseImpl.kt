package com.company.vansales.app.framework

import com.company.vansales.app.domain.repos.ApprovalsStatusRepo
import com.company.vansales.app.domain.usecases.ApprovalsStatusUseCases
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

class ApprovalsStatusUseCaseImpl (
    val approvalsStatusRepo: ApprovalsStatusRepo
) : ApprovalsStatusUseCases.ApprovalsStatusInvoke {
    override suspend fun invoke(approvalsStatusRequestModel: ApprovalsStatusRequestModel):
            ApiResponse<BaseResponse<ApprovalsStatusResponseModel>, Error> =
                approvalsStatusRepo.approvalsStatus(approvalsStatusRequestModel)

}