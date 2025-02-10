package com.company.vansales.app.domain.repos

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface OpenInvoicesRepo {
    suspend fun getOpenInvoices(openInvoicesRequestModel: OpenInvoicesRequestModel):
            ApiResponse<BaseResponse<OpenInvoicesResponseModel>, Error>

}