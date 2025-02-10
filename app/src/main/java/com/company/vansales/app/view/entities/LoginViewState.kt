package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.LoginResponseModel

sealed class LoginViewState {
    data class  Loading(val show :Boolean) : LoginViewState()
    data class  LoginSuccess(val data : LoginResponseModel) : LoginViewState()
    data class  LoginFailure(val error : Error) : LoginViewState()
    object  NetworkFailure : LoginViewState()
}