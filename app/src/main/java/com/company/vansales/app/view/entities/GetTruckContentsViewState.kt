package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.TruckResponse

sealed class GetTruckContentsViewState {
    data class  Loading(val show :Boolean) : GetTruckContentsViewState()
    object  NetworkFailure : GetTruckContentsViewState()
    data class  Data(val data : TruckResponse) : GetTruckContentsViewState()
}