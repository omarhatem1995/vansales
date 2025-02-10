package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.CancelDocumentRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface CancelDocumentRepo {
    suspend fun cancelDocument(cancelDocumentRequestModel: CancelDocumentRequestModel):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error>

}