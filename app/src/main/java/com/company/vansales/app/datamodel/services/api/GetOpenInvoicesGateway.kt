package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.OpenInvoicesRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.OpenInvoicesResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface GetOpenInvoicesGateway {

    @POST("Document/openInvoices")
    suspend fun getOpenInvoices(@Body openInvoicesRequestModel: OpenInvoicesRequestModel):
            ApiResponse<BaseResponse<OpenInvoicesResponseModel>, Error>

}