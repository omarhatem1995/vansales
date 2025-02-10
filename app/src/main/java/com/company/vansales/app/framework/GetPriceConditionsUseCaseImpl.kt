package com.company.vansales.app.framework

import com.company.vansales.app.domain.repos.PriceConditionsRepo
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel
import com.company.vansales.app.domain.usecases.GetPriceConditionsUseCases

class GetPriceConditionsUseCaseImpl (
    val priceConditionsRepo : PriceConditionsRepo
) : GetPriceConditionsUseCases.GetPriceConditionsInvoke {
    override suspend fun invoke(pricesRequestModel: PricesRequestModel):
            ApiResponse<BaseResponse<ItemPriceCondition>, Error> =
        priceConditionsRepo.getPriceConditions(pricesRequestModel)
}