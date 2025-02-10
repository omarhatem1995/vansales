package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigResponseModel

interface VanSalesConfigRepo {
    suspend fun vanSalesConfig(vanSalesConfigRequestModel: VanSalesConfigRequestModel):
            ApiResponse<BaseResponse<VanSalesConfigResponseModel>, Error>
}