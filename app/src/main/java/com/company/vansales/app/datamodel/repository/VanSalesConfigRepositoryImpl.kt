package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigResponseModel
import com.company.vansales.app.datamodel.services.api.VanSalesConfigGateway
import com.company.vansales.app.domain.repos.VanSalesConfigRepo

class VanSalesConfigRepositoryImpl (
    private val vanSalesConfigGateway: VanSalesConfigGateway
    ) : VanSalesConfigRepo {

    override suspend fun vanSalesConfig(vanSalesConfigRequestModel: VanSalesConfigRequestModel):
            ApiResponse<BaseResponse<VanSalesConfigResponseModel>, Error> {
        return vanSalesConfigGateway.vanSalesConfig(vanSalesConfigRequestModel)
    }

}