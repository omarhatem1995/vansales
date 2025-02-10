package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeResponseModel

interface NumberRangeUseCases {

    interface View {
        fun renderNumberRange(data : NumberRangeResponseModel)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderNumberRangeFailure()
    }
    interface NumberRangeInvoke {
        suspend fun invoke(numberRangeRequestModel: NumberRangeRequestModel) :
                ApiResponse<BaseResponse<NumberRangeResponseModel>, Error>
    }
    }