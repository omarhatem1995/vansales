package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationResponse
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationRequestBody
import com.company.vansales.app.domain.repos.StorageLocRepo
import com.company.vansales.app.domain.usecases.StorageLocationUseCases

class StorageLocUseCaseImpl (
    val storageLocRepo: StorageLocRepo
) : StorageLocationUseCases.StorageLocInvoke {

    override suspend fun invoke(storageLocationRequestBody: StorageLocationRequestBody):
            ApiResponse<BaseResponse<StorageLocationResponse>, Error> =
                storageLocRepo.storageLocation(storageLocationRequestBody)

}