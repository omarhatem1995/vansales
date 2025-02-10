package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.TruckResponse
import com.company.vansales.app.datamodel.services.api.GetTruckContentsGateway
import com.company.vansales.app.domain.repos.TruckContentsRepo

class GetTruckContentRepositoryImpl (
    private val getTruckContentsGateway: GetTruckContentsGateway
    ) : TruckContentsRepo {

    override suspend fun getTruckContents(baseBody: BaseBody):
            ApiResponse<TruckResponse, Error> {
        return getTruckContentsGateway.getTruckMaterials(baseBody)
    }

}