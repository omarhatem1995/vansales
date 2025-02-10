package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationRequestBody
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationResponse
import com.company.vansales.app.datamodel.services.api.StorageLocGateway
import com.company.vansales.app.domain.repos.StorageLocRepo

class StorageLocationRepositoryImpl (
    private val storageLocGateway: StorageLocGateway
    ) : StorageLocRepo {
    override suspend fun storageLocation(storageLocationRequestBody: StorageLocationRequestBody):
            ApiResponse<BaseResponse<StorageLocationResponse>, Error> {
        return storageLocGateway.storageLocation(storageLocationRequestBody)
    }
}