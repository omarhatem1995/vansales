package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayResponseModel
import com.company.vansales.app.datamodel.services.api.EndOfDayGateway
import com.company.vansales.app.domain.repos.EndOfDayRepo

class EndOfDayRepositoryImpl (
    private val endOfDayGateway: EndOfDayGateway
    ) : EndOfDayRepo {
    override suspend fun endOfDay(endOfDayRequestModel: EndOfDayRequestModel):
            ApiResponse<EndOfDayResponseModel, Error> {
        return endOfDayGateway.endOfDay(endOfDayRequestModel)
    }
}