package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.CollectionsRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.CollectionsResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface CollectionsGateway {

    @POST("Document/collectedInvoices")
    suspend fun getCollections(@Body collectionsRequestModel: CollectionsRequestModel):
            ApiResponse<BaseResponse<CollectionsResponseModel>, Error>

}