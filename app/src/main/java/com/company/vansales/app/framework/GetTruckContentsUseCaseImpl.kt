package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.mastermodels.TruckResponse
import com.company.vansales.app.domain.repos.TruckContentsRepo
import com.company.vansales.app.domain.usecases.GetTruckContentsUseCases
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody

class GetTruckContentsUseCaseImpl (
    val truckContentsRepo: TruckContentsRepo
) : GetTruckContentsUseCases.GetTruckContentsInvoke {
    override suspend fun invoke(baseBody: BaseBody):
            ApiResponse<TruckResponse, Error> =
                truckContentsRepo.getTruckContents(baseBody)
}