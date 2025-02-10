package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayResponseModel
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface EndOfDayRepo {

    suspend fun endOfDay(endOfDayRequestModel: EndOfDayRequestModel):
            ApiResponse<EndOfDayResponseModel, Error>

}