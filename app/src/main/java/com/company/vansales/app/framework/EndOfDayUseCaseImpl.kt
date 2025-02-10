package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.EndOfDayResponseModel
import com.company.vansales.app.domain.repos.EndOfDayRepo
import com.company.vansales.app.domain.usecases.EndOfDayUseCases
import com.company.vansales.app.datamodel.models.base.ApiResponse

class EndOfDayUseCaseImpl (
    private val endOfDayRepo: EndOfDayRepo
) : EndOfDayUseCases.EndOfDayInvoke {
    override suspend fun invoke(endOfDayRequestModel: EndOfDayRequestModel):
            ApiResponse<EndOfDayResponseModel, Error> =
                endOfDayRepo.endOfDay(endOfDayRequestModel)

}