package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.CancelDocumentRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface CancelDocumentGateway {

    @POST("Document/create")
    suspend fun cancelDocument(@Body salesDocBody: CancelDocumentRequestModel):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error>

}