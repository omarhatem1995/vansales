package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.CreateCustomer
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.services.api.CreateCustomerGateway
import com.company.vansales.app.domain.repos.CreateCustomerRepo

class CreateCustomerRepositoryImpl (
    private val createCustomerGateway: CreateCustomerGateway
    ) : CreateCustomerRepo {

    override suspend fun createCustomer(createCustomerModel: CreateCustomer):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error> {
        return createCustomerGateway.createCustomer(createCustomerModel)
    }

}