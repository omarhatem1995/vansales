package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginClerkGateway {

    @POST("login/clerk")
    suspend fun login(@Body loginClerkRequestModel: LoginClerkRequestModel):
            ApiResponse<LoginClerkResponseModel, Error>

}