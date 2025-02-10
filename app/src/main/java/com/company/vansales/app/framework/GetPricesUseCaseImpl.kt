package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.domain.repos.PricesRepo
import com.company.vansales.app.domain.usecases.GetPricesUseCases

public class GetPricesUseCaseImpl (
    val pricesRepo : PricesRepo
) : GetPricesUseCases.GetPricesInvoke {
    override suspend fun invoke(pricesRequestModel: PricesRequestModel):
            ApiResponse<BaseResponse<Prices>, Error> =
                pricesRepo.getPrices(pricesRequestModel)
}