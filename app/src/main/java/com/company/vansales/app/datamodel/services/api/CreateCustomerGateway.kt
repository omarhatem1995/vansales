package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.CreateCustomer
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateCustomerGateway {

    @POST("Customers/create")
    suspend fun createCustomer(@Body createCustomer: CreateCustomer):
            ApiResponse<BaseResponse<PostDocumentResponse>, Error>

}