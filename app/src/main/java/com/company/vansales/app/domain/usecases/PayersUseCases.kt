package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface PayersUseCases {

    interface View {
        fun renderPayersSuccess(data : PayersResponseModel)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderPayersFailure()
    }
    interface GetPayersInvoke {
        suspend fun invoke(payersRequestModel: PayersRequestModel) :
                ApiResponse<PayersResponseModel, Error>
    }
    }