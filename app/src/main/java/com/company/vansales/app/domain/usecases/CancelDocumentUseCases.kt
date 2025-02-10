package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.CancelDocumentRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface CancelDocumentUseCases {

    interface View {
        fun renderCancelDocument(data : BaseResponse<PostDocumentResponse>)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderCancelDocumentError()
    }
    interface CancelDocumentInvoke {
        suspend fun invoke(cancelDocumentRequestModel: CancelDocumentRequestModel) :
                ApiResponse<BaseResponse<PostDocumentResponse>, Error>
    }
    }