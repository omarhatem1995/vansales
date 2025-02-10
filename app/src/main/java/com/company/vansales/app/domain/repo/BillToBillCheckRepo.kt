package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckRequestModel

interface BillToBillCheckRepo {
    suspend fun billToBillCheck(billToBillCheckRequestModel: BillToBillCheckRequestModel):
            ApiResponse<BillToBillCheckResponseModel, Error>
}