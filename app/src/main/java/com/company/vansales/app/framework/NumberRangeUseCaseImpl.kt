package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.domain.repos.ApprovalsStatusRepo
import com.company.vansales.app.domain.repos.BillToBillCheckRepo
import com.company.vansales.app.domain.repos.CreateCustomerRepo
import com.company.vansales.app.domain.repos.NumberRangeRepo
import com.company.vansales.app.domain.usecases.ApprovalsStatusUseCases
import com.company.vansales.app.domain.usecases.BillToBillCheckUseCases
import com.company.vansales.app.domain.usecases.CreateCustomerUseCases
import com.company.vansales.app.domain.usecases.NumberRangeUseCases

class NumberRangeUseCaseImpl (
    val numberRangeRepo: NumberRangeRepo
) : NumberRangeUseCases.NumberRangeInvoke {
    override suspend fun invoke(numberRangeRequestModel: NumberRangeRequestModel):
            ApiResponse<BaseResponse<NumberRangeResponseModel>, Error> =
                numberRangeRepo.numberRange(numberRangeRequestModel)

}