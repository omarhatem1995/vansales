package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.LoginRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginResponseModel
import com.company.vansales.app.datamodel.services.api.LoginGateway
import com.company.vansales.app.domain.repo.LoginRepo


class LoginRepositoryImpl (
    private val loginGateway: LoginGateway
    ) : LoginRepo {
    override suspend fun login(loginRequestModel: LoginRequestModel): ApiResponse<LoginResponseModel, Error> {
        return loginGateway.login(loginRequestModel)
    }
}