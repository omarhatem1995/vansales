package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.mastermodels.DriverDataRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.DriverDataResponseModel
import com.company.vansales.app.domain.repos.DriverDataRepo
import com.company.vansales.app.domain.usecases.DriverDataUseCases
import com.company.vansales.app.datamodel.models.base.ApiResponse

class DriverDataUseCaseImpl (
    private val driverDataRepo: DriverDataRepo
) : DriverDataUseCases.DriverDataInvoke {
    override suspend fun invoke(driverDataRequestModel: DriverDataRequestModel):
            ApiResponse<DriverDataResponseModel, Error> =
                driverDataRepo.driverData(driverDataRequestModel)

}