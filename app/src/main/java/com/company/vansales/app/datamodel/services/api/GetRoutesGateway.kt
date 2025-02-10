package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import retrofit2.http.Body
import retrofit2.http.POST

interface GetRoutesGateway {

    @POST("Routes/find")
    suspend fun getRoutes(@Body baseBody: BaseBody):
            ApiResponse<BaseResponse<Routes>, Error>

}