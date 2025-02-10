package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.HelpView

sealed class GetHelpViewState {
    data class  Loading(val show :Boolean) : GetHelpViewState()
    object  NetworkFailure : GetHelpViewState()
    data class  Data(val data : BaseResponse<HelpView>) : GetHelpViewState()
    data class  FilteredDataByLanguage(val data : List<HelpView>) : GetHelpViewState()
}