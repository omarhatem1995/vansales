package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody

interface GetTruckContentsUseCases {
    interface View {
        fun renderTruckContents(data: Truck)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface GetTruckContentsInvoke {
        suspend fun invoke(baseBody: BaseBody) :
                ApiResponse<TruckResponse, Error>
    }
}