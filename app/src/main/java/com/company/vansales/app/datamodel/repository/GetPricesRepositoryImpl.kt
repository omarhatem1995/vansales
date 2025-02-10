package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel
import com.company.vansales.app.datamodel.services.api.GetPricesGateway
import com.company.vansales.app.domain.repos.PricesRepo

class GetPricesRepositoryImpl (
    private val getPricesGateway: GetPricesGateway
    ) : PricesRepo {

    override suspend fun getPrices(pricesRequestModel: PricesRequestModel):
            ApiResponse<BaseResponse<Prices>, Error> {
        return getPricesGateway.getPrices(pricesRequestModel)
    }

}