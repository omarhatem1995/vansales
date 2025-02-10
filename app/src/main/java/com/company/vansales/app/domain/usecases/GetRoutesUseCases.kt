package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Routes

interface GetRoutesUseCases {
    interface View {
        fun renderRoutesList(data: List<Routes>)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface GetRoutesInvoke {
        suspend fun invoke(baseBody: BaseBody) :
                ApiResponse<BaseResponse<Routes>, Error>
    }
}