package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel
import retrofit2.http.Body
import retrofit2.http.POST

interface GetPricesGateway {
    @POST("Prices/find")
    suspend fun getPrices(@Body pricesRequestModel: PricesRequestModel):
            ApiResponse<BaseResponse<Prices>, Error>

}