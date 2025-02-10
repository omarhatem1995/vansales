package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.services.api.CreateDocumentGateway
import com.company.vansales.app.domain.repos.CreateDocumentRepo

class CreateDocumentRepositoryImpl (
    private val createDocumentGateway: CreateDocumentGateway
    ) : CreateDocumentRepo {

    override suspend fun createDocument(salesDocBody: SalesDocBody):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error> {
        return createDocumentGateway.createDocument(salesDocBody)
    }

}