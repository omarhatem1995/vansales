package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginClerkResponseModel
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface LoginClerkUseCases {
    interface View {
        fun renderLoginClerkSuccess(data: LoginClerkResponseModel)
        fun renderLoginClerkSuccessWrong(data: String)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface LoginClerkInvoke {
        suspend fun invoke(loginClerkRequestModel: LoginClerkRequestModel) :
                ApiResponse<LoginClerkResponseModel, Error>
    }
}