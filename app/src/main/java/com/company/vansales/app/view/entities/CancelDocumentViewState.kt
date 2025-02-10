package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse


sealed class CancelDocumentViewState {
    data class  Loading(val show :Boolean) : CancelDocumentViewState()
    object  NetworkFailure : CancelDocumentViewState()
    data class  Data(val data : BaseResponse<PostDocumentResponse>) : CancelDocumentViewState()
}