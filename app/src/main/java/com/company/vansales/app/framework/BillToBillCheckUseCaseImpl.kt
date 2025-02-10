package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.domain.repos.BillToBillCheckRepo
import com.company.vansales.app.domain.usecases.BillToBillCheckUseCases
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckRequestModel

class BillToBillCheckUseCaseImpl (
    val billToBillCheckRepo: BillToBillCheckRepo
) : BillToBillCheckUseCases.BillToBillCheckInvoke {
    override suspend fun invoke(billToBillCheckRequestModel: BillToBillCheckRequestModel):
            ApiResponse<BillToBillCheckResponseModel, Error> =
                billToBillCheckRepo.billToBillCheck(billToBillCheckRequestModel)

}