package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.HelpView

interface GetHelpUseCases {
    interface View {
        fun renderHelpList(data: List<HelpView>)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface GetHelpInvoke {
        suspend fun invoke(baseBody: BaseBody) :
                ApiResponse<BaseResponse<HelpView>, Error>
    }
}