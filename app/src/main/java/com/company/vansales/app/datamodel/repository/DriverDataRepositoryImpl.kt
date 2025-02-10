package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.DriverDataRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.DriverDataResponseModel
import com.company.vansales.app.datamodel.services.api.DriverDataGateway
import com.company.vansales.app.domain.repos.DriverDataRepo

class DriverDataRepositoryImpl (
    private val driverDataGateway: DriverDataGateway
) : DriverDataRepo {
    override suspend fun driverData(driverDataRequestModel: DriverDataRequestModel):
            ApiResponse<DriverDataResponseModel, Error> {
        return driverDataGateway.getCustomer(driverDataRequestModel)
    }
}