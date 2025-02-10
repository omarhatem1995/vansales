package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse


sealed class CreateDocumentViewState {
    data class  Loading(val show :Boolean) : CreateDocumentViewState()
    object  NetworkFailure : CreateDocumentViewState()
    object  EmptyResponse : CreateDocumentViewState()
    object  UnknownError : CreateDocumentViewState()
    data class  Data(val data : BaseResponse<PostDocumentResponse>) : CreateDocumentViewState()
}