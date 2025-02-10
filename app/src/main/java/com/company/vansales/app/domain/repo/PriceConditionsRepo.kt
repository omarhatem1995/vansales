package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel

interface PriceConditionsRepo {
    suspend fun getPriceConditions(pricesRequestModel: PricesRequestModel):
            ApiResponse<BaseResponse<ItemPriceCondition>, Error>
}