package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel

interface GetPriceConditionsUseCases {
    interface View {
        fun renderConditionLoadedValue(data: String)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface GetPriceConditionsInvoke {
        suspend fun invoke(pricesRequestModel: PricesRequestModel) :
                ApiResponse<BaseResponse<ItemPriceCondition>, Error>
    }
}