package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationRequestBody
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface StorageLocGateway {

    @POST("storageLoc/get")
    suspend fun storageLocation(@Body storageLocationRequestBody: StorageLocationRequestBody):
            ApiResponse<BaseResponse<StorageLocationResponse>, Error>

}