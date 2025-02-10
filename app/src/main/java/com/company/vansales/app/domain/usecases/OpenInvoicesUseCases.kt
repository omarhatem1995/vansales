package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface OpenInvoicesUseCases {
    interface View {
        fun renderOpenInvoices(data: BaseResponse<OpenInvoicesResponseModel>)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface GetOpenInvoicesInvoke {
        suspend fun invoke(openInvoicesRequestModel: OpenInvoicesRequestModel) :
                ApiResponse<BaseResponse<OpenInvoicesResponseModel>, Error>
    }
}