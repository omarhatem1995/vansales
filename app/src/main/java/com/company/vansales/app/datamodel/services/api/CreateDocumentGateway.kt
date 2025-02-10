package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateDocumentGateway {

    @POST("Document/create")
    suspend fun createDocument(@Body salesDocBody: SalesDocBody):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error>

}