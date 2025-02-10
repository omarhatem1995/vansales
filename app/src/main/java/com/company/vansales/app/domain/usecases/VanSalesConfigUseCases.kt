package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigResponseModel

interface VanSalesConfigUseCases {

    interface View {
        fun renderVanSalesConfigSuccess(data : List<VanSalesConfigResponseModel>)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderVanSalesConfigError()
    }
    interface VanSalesConfigInvoke {
        suspend fun invoke(vanSalesConfigRequestModel: VanSalesConfigRequestModel) :
                ApiResponse<BaseResponse<VanSalesConfigResponseModel>, Error>
    }
    }