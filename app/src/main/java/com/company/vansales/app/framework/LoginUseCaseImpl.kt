package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.LoginRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginResponseModel
import com.company.vansales.app.domain.repo.LoginRepo
import com.company.vansales.app.domain.usecases.LoginUseCases


class LoginUseCaseImpl (
    private val loginRepo: LoginRepo
) : LoginUseCases.LoginInvoke {
    override suspend fun invoke(loginRequestModel: LoginRequestModel):
            ApiResponse<LoginResponseModel, Error> =
                loginRepo.login(loginRequestModel)

}