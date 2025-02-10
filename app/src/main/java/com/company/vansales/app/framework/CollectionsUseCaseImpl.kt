package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.domain.repos.CollectionsRepo
import com.company.vansales.app.domain.usecases.CollectionsUseCases
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

class CollectionsUseCaseImpl (
    val collectionsRepo: CollectionsRepo
) : CollectionsUseCases.GetCollectionsInvoke {
    override suspend fun invoke(collectionsRequestModel: CollectionsRequestModel):
            ApiResponse<BaseResponse<CollectionsResponseModel>, Error> =
                collectionsRepo.getCollections(collectionsRequestModel)
}