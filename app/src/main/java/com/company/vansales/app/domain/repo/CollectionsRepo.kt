package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface CollectionsRepo {
    suspend fun getCollections(collectionsRequestModel: CollectionsRequestModel):
            ApiResponse<BaseResponse<CollectionsResponseModel>, Error>

}