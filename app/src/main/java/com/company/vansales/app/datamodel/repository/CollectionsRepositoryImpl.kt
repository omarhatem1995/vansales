package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.CollectionsRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.CollectionsResponseModel
import com.company.vansales.app.datamodel.services.api.CollectionsGateway
import com.company.vansales.app.domain.repos.CollectionsRepo

class CollectionsRepositoryImpl (
    private val collectionsGateway: CollectionsGateway
    ) : CollectionsRepo {
    override suspend fun getCollections(collectionsRequestModel: CollectionsRequestModel):
            ApiResponse<BaseResponse<CollectionsResponseModel>, Error> {
        return collectionsGateway.getCollections(collectionsRequestModel)
    }
}