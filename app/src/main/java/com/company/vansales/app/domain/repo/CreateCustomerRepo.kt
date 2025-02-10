package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.CreateCustomer
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface CreateCustomerRepo {
    suspend fun createCustomer(createCustomerModel : CreateCustomer):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error>

}