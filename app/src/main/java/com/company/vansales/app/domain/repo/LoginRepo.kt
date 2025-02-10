package com.company.vansales.app.domain.repo

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.LoginRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginResponseModel


interface LoginRepo {

    suspend fun login(loginRequestModel: LoginRequestModel):
            ApiResponse<LoginResponseModel, Error>

}