package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.Routes

sealed class GetRoutesViewState {
    data class  Loading(val show :Boolean) : GetRoutesViewState()
    object  NetworkFailure : GetRoutesViewState()
    data class  RoutesData(val data : List<Routes>) : GetRoutesViewState()
}