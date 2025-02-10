package com.company.vansales.app.domain.repo

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Routes

interface RoutesRepo {
    suspend fun getRoutes(baseBody : BaseBody):
            ApiResponse<BaseResponse<Routes>, Error>

}