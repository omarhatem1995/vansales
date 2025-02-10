package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.domain.repo.RoutesRepo
import com.company.vansales.app.domain.usecases.GetRoutesUseCases

class GetRoutesUseCaseImpl (
    private val routesRepo: RoutesRepo
) : GetRoutesUseCases.GetRoutesInvoke {
    override suspend fun invoke(baseBody: BaseBody):
            ApiResponse<BaseResponse<Routes>, Error> =
                routesRepo.getRoutes(baseBody)
}