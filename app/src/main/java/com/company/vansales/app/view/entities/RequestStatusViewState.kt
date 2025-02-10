package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckResponseModel


sealed class RequestStatusViewState {
    data class  Loading(val show :Boolean) : RequestStatusViewState()
    object  NetworkFailure : RequestStatusViewState()
    data class  Data(val data : BillToBillCheckResponseModel) : RequestStatusViewState()
}