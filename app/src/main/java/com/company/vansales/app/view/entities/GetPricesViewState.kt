package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.Prices

sealed class GetPricesViewState {
    data class  Loading(val show :Boolean) : GetPricesViewState()
    object  NetworkFailure : GetPricesViewState()
    data class  Data(val data : List<Prices>) : GetPricesViewState()
    data class  LoadingMessage(val data : String) : GetPricesViewState()
}