package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeResponseModel
import com.company.vansales.app.datamodel.services.api.NumberRangeGateway
import com.company.vansales.app.domain.repos.NumberRangeRepo

class NumberRangeRepositoryImpl (
    private val numberRangeGateway: NumberRangeGateway
    ) : NumberRangeRepo {

    override suspend fun numberRange(numberRangeRequestModel: NumberRangeRequestModel):
            ApiResponse<BaseResponse<NumberRangeResponseModel>, Error> {
        return numberRangeGateway.numberRange(numberRangeRequestModel)
    }

}