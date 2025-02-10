package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationResponse
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationRequestBody
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface StorageLocRepo {

    suspend fun storageLocation(storageLocationRequestBody: StorageLocationRequestBody):
            ApiResponse<BaseResponse<StorageLocationResponse>, Error>

}