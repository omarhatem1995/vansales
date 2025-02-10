package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition

sealed class GetPriceConditionsViewState {
    data class  Loading(val show :Boolean) : GetPriceConditionsViewState()
    object  NetworkFailure : GetPriceConditionsViewState()
    data class  Data(val data : List<ItemPriceCondition>) : GetPriceConditionsViewState()
    data class  LoadingMessage(val data : String) : GetPriceConditionsViewState()
}