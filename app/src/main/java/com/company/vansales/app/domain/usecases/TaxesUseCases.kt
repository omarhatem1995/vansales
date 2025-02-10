package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.models.mastermodels.TaxesRequestModel

interface TaxesUseCases {

    interface View {
        fun renderTaxes(data : List<Taxes>)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
    }
    interface TaxesInvoke {
        suspend fun invoke(taxesRequestModel: TaxesRequestModel) :
                ApiResponse<BaseResponse<Taxes>, Error>
    }
    }