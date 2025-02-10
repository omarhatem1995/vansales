package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckResponseModel


sealed class BillToBillCheckViewState {
    data class  Loading(val show :Boolean) : BillToBillCheckViewState()
    object  NetworkFailure : BillToBillCheckViewState()
    data class  Data(val data : BillToBillCheckResponseModel) : BillToBillCheckViewState()
}