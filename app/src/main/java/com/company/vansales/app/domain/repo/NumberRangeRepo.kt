package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeResponseModel

interface NumberRangeRepo {
    suspend fun numberRange(numberRangeRequestModel: NumberRangeRequestModel):
            ApiResponse<BaseResponse<NumberRangeResponseModel>, Error>
}