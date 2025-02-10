package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.DriverDataResponseModel


sealed class DriverDataViewState {
    data class  Loading(val show :Boolean) : DriverDataViewState()
    object  NetworkFailure : DriverDataViewState()
    data class  DriverDataSuccess(val data : DriverDataResponseModel) : DriverDataViewState()
    data class  DriverDataFailure(val error : Error) : DriverDataViewState()
}