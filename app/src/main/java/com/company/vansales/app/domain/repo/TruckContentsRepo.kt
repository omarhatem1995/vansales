package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.TruckResponse
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody

interface TruckContentsRepo {
    suspend fun getTruckContents(baseBody : BaseBody):
            ApiResponse<TruckResponse, Error>

}