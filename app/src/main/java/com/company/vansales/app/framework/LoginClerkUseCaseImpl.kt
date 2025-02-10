package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkResponseModel
import com.company.vansales.app.domain.repos.LoginClerkRepo
import com.company.vansales.app.domain.usecases.LoginClerkUseCases

class LoginClerkUseCaseImpl (
    private val loginClerkRepo: LoginClerkRepo
) : LoginClerkUseCases.LoginClerkInvoke {
    override suspend fun invoke(loginClerkRequestModel: LoginClerkRequestModel):
            ApiResponse<LoginClerkResponseModel, Error> =
                loginClerkRepo.login(loginClerkRequestModel)

}