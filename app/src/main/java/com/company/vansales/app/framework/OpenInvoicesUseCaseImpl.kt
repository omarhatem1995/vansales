package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.domain.repos.OpenInvoicesRepo
import com.company.vansales.app.domain.usecases.OpenInvoicesUseCases

class OpenInvoicesUseCaseImpl (
    val openInvoicesRepo: OpenInvoicesRepo
) : OpenInvoicesUseCases.GetOpenInvoicesInvoke {
    override suspend fun invoke(openInvoicesRequestModel: OpenInvoicesRequestModel):
            ApiResponse<BaseResponse<OpenInvoicesResponseModel>, Error> =
                openInvoicesRepo.getOpenInvoices(openInvoicesRequestModel)
}