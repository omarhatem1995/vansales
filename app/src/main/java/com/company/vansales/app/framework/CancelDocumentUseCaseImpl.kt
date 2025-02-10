package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.mastermodels.CancelDocumentRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.domain.repos.CancelDocumentRepo
import com.company.vansales.app.domain.usecases.CancelDocumentUseCases
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

class CancelDocumentUseCaseImpl (
    val cancelDocumentRepo: CancelDocumentRepo
) : CancelDocumentUseCases.CancelDocumentInvoke {
    override suspend fun invoke(cancelDocumentRequestModel: CancelDocumentRequestModel):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error> =
                cancelDocumentRepo.cancelDocument(cancelDocumentRequestModel)

}