package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.PayersResponseModel


sealed class PayersViewState {
    data class  Loading(val show :Boolean) : PayersViewState()
    object  NetworkFailure : PayersViewState()
    data class  Data(val data : PayersResponseModel) : PayersViewState()
}