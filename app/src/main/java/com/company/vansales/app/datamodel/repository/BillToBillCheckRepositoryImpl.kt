package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckResponseModel
import com.company.vansales.app.datamodel.services.api.BillToBillCheckGateway
import com.company.vansales.app.domain.repos.BillToBillCheckRepo

class BillToBillCheckRepositoryImpl (
    private val billToBillCheckGateway: BillToBillCheckGateway
    ) : BillToBillCheckRepo {

    override suspend fun billToBillCheck(billToBillCheckRequestModel: BillToBillCheckRequestModel):
            ApiResponse<BillToBillCheckResponseModel, Error> {
        return billToBillCheckGateway.billToBillCheck(billToBillCheckRequestModel)
    }

}