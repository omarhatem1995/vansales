package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayResponseModel
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface EndOfDayUseCases {
    interface View {
        fun renderEndOfDaySuccess(data: EndOfDayResponseModel)
        fun renderEndOfDaySuccessWrong(data: String)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface EndOfDayInvoke {
        suspend fun invoke(endOfDayRequestModel: EndOfDayRequestModel) :
                ApiResponse<EndOfDayResponseModel, Error>
    }
}