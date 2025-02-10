package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.LoginRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginGateway {

    @POST("login/check")
    suspend fun login(@Body loginRequestModel: LoginRequestModel):
            ApiResponse<LoginResponseModel, Error>

}