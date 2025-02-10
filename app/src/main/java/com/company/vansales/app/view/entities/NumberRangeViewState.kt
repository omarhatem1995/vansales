package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeResponseModel

sealed class NumberRangeViewState {
    data class  Loading(val show :Boolean) : NumberRangeViewState()
    object  NetworkFailure : NumberRangeViewState()
    data class  Data(val data : List<NumberRangeResponseModel>) : NumberRangeViewState()
}