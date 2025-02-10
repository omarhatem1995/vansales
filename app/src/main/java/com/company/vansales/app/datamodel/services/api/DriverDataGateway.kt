package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.DriverDataRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.DriverDataResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface DriverDataGateway {

    @POST("Customers/findone")
    suspend fun getCustomer(@Body driverRequestModel : DriverDataRequestModel):
            ApiResponse<DriverDataResponseModel, Error>

}