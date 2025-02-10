package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigResponseModel

sealed class VanSalesConfigViewState {
    data class  Loading(val show :Boolean) : VanSalesConfigViewState()
    object  NetworkFailure : VanSalesConfigViewState()
    data class  Data(val data : List<VanSalesConfigResponseModel>) : VanSalesConfigViewState()
}