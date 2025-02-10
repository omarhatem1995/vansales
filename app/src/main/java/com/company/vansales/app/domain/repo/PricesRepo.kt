package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel

interface PricesRepo {
    suspend fun getPrices(pricesRequestModel: PricesRequestModel):
            ApiResponse<BaseResponse<Prices>, Error>
}