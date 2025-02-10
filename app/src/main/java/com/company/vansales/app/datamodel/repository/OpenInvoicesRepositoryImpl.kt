package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.OpenInvoicesRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.OpenInvoicesResponseModel
import com.company.vansales.app.datamodel.services.api.GetOpenInvoicesGateway
import com.company.vansales.app.domain.repos.OpenInvoicesRepo

class OpenInvoicesRepositoryImpl (
    private val openInvoicesGateway: GetOpenInvoicesGateway
    ) : OpenInvoicesRepo {
    override suspend fun getOpenInvoices(openInvoicesRequestModel: OpenInvoicesRequestModel):
            ApiResponse<BaseResponse<OpenInvoicesResponseModel>, Error> {
        return openInvoicesGateway.getOpenInvoices(openInvoicesRequestModel)
    }
}