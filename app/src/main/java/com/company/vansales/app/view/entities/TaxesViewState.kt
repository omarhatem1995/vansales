package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.Taxes

sealed class TaxesViewState {
    data class  Loading(val show :Boolean) : TaxesViewState()
    object  NetworkFailure : TaxesViewState()
    data class  Data(val data : List<Taxes>) : TaxesViewState()
}