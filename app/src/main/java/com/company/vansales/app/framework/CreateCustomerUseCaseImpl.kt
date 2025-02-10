package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.mastermodels.CreateCustomer
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.domain.repos.CreateCustomerRepo
import com.company.vansales.app.domain.usecases.CreateCustomerUseCases
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

class CreateCustomerUseCaseImpl (
    val createCustomerRepo: CreateCustomerRepo
) : CreateCustomerUseCases.CreateCustomerInvoke {
    override suspend fun invoke(createCustomerModel: CreateCustomer):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error> =
                createCustomerRepo.createCustomer(createCustomerModel)

}