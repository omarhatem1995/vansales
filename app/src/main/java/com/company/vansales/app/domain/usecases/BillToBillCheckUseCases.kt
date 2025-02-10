package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckRequestModel

interface BillToBillCheckUseCases {

    interface View {
        fun renderBillToBillCheck(data : BillToBillCheckResponseModel)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderBillToBillCheckError()
    }
    interface BillToBillCheckInvoke {
        suspend fun invoke(billToBillCheckRequestModel: BillToBillCheckRequestModel) :
                ApiResponse<BillToBillCheckResponseModel, Error>
    }
    }