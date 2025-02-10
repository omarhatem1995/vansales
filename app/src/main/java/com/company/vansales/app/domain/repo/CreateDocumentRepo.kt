package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import io.reactivex.Observable

interface CreateDocumentRepo {
    suspend fun createDocument(salesDocBody : SalesDocBody):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error>

}