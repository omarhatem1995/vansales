package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse

interface DriverDataUseCases {
    interface View {
        fun renderDriverData(data : DriverDataResponseModel)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderDriverDataError()
    }
    interface DriverDataInvoke {
        suspend fun invoke(driverDataRequestModel: DriverDataRequestModel) :
                ApiResponse<DriverDataResponseModel, Error>
    }
}