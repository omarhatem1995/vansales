package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface VanSalesConfigGateway {

    @POST("config/vs")
    suspend fun vanSalesConfig(@Body vanSalesConfigRequestModel: VanSalesConfigRequestModel):
            ApiResponse<BaseResponse<VanSalesConfigResponseModel>, Error>

}