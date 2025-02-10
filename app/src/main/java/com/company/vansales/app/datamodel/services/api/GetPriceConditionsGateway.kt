package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface GetPriceConditionsGateway {

    @POST("Conditions/find")
    suspend fun getItemPriceCondition(@Body pricesRequestModel: PricesRequestModel):
            ApiResponse<BaseResponse<ItemPriceCondition>, Error>

}