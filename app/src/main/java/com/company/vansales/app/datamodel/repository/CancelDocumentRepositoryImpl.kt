package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.CancelDocumentRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.services.api.CancelDocumentGateway
import com.company.vansales.app.domain.repos.CancelDocumentRepo

class CancelDocumentRepositoryImpl (
    private val cancelDocumentGateway: CancelDocumentGateway
    ) : CancelDocumentRepo {

    override suspend fun cancelDocument(cancelDocumentRequestModel: CancelDocumentRequestModel):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error> {
        return cancelDocumentGateway.cancelDocument(cancelDocumentRequestModel)
    }

}