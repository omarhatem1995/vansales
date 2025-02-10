package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.CreateCustomer
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface CreateCustomerUseCases {

    interface View {
        fun renderCreateCustomer(data : BaseResponse<PostDocumentResponse>)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderCreateCustomerError()
    }
    interface CreateCustomerInvoke {
        suspend fun invoke(createCustomerModel : CreateCustomer) :
                ApiResponse<BaseResponse<PostDocumentResponse>, Error>
    }
    }