package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkResponseModel
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface LoginClerkRepo {

    suspend fun login(loginClerkRequestModel: LoginClerkRequestModel):
            ApiResponse<LoginClerkResponseModel, Error>

}