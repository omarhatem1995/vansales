package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface RequestStatusUseCases {

    interface View {
        fun renderRequestStatus(data : BillToBillCheckResponseModel,totalValueNormalCreditLimit:String,totalValueTopUpCreditLimit : String)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderRequestStatusError()
    }
    interface RequestStatusInvoke {
        suspend fun invoke(requestStatusRequestModel: RequestStatusRequestModel) :
                ApiResponse<BillToBillCheckResponseModel, Error>
    }
    }