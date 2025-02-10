package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.TruckResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface GetTruckContentsGateway {

    @POST("Truck/find")
    suspend fun getTruckMaterials(@Body baseBody: BaseBody):
            ApiResponse<TruckResponse, Error>
}