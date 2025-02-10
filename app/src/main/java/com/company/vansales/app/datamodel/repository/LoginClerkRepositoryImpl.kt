package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkResponseModel
import com.company.vansales.app.datamodel.services.api.LoginClerkGateway
import com.company.vansales.app.domain.repos.LoginClerkRepo

class LoginClerkRepositoryImpl (
    private val loginClerkGateway: LoginClerkGateway
    ) : LoginClerkRepo {
    override suspend fun login(loginClerkRequestModel: LoginClerkRequestModel):
            ApiResponse<LoginClerkResponseModel, Error> {
        return loginClerkGateway.login(loginClerkRequestModel)
    }
}