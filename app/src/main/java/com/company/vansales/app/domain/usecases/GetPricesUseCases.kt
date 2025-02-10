package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel

interface GetPricesUseCases {
    interface View {
        fun renderPricesLoadedValue(data: String)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface GetPricesInvoke {
        suspend fun invoke(pricesRequestModel: PricesRequestModel) :
                ApiResponse<BaseResponse<Prices>, Error>
    }
}