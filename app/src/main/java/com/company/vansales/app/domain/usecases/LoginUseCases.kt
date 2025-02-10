package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.LoginRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginResponseModel

interface LoginUseCases {
    interface View {
        fun renderLoginSuccess(data: LoginResponseModel)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface LoginInvoke {
        suspend fun invoke(loginRequestModel: LoginRequestModel) :
                ApiResponse<LoginResponseModel, Error>
    }
}