package com.company.vansales.app.datamodel.repository

import android.util.Log
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel
import com.company.vansales.app.datamodel.services.api.GetPriceConditionsGateway
import com.company.vansales.app.domain.repos.PriceConditionsRepo

class GetPriceConditionsRepositoryImpl (
    private val getPriceConditionsGateway: GetPriceConditionsGateway
) : PriceConditionsRepo {

    override suspend fun getPriceConditions(pricesRequestModel: PricesRequestModel):
            ApiResponse<BaseResponse<ItemPriceCondition>, Error> {
        Log.d("getConditions", "RepoImpl is called ")

        return getPriceConditionsGateway.getItemPriceCondition(pricesRequestModel)
    }

}