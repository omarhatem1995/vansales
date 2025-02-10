package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.domain.repos.CreateDocumentRepo
import com.company.vansales.app.domain.usecases.CreateDocumentUseCases
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

class CreateDocumentUseCaseImpl (
    val createDocumentRepo: CreateDocumentRepo
) : CreateDocumentUseCases.CreateDocumentInvoke {
    override suspend fun invoke(salesDocBody: SalesDocBody):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error> =
                createDocumentRepo.createDocument(salesDocBody)

}