package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.DriverDataRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.DriverDataResponseModel
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface DriverDataRepo {
    suspend fun driverData(driverDataRequestModel: DriverDataRequestModel):
            ApiResponse<DriverDataResponseModel, Error>
}