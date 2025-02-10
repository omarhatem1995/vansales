package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.ApprovalsStatusResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface ApprovalsStatusUseCases {

    interface View {
        fun renderApprovalsStatus(data : List<ApprovalsStatusResponseModel>)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderApprovalsStatusError()
    }
    interface ApprovalsStatusInvoke {
        suspend fun invoke(approvalsStatusRequestModel: ApprovalsStatusRequestModel) :
                ApiResponse<BaseResponse<ApprovalsStatusResponseModel>, Error>
    }
    }