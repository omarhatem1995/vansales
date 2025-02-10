package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.domain.repos.TaxesRepo
import com.company.vansales.app.domain.usecases.TaxesUseCases

class TaxesUseCaseImpl (
    val taxesRepo: TaxesRepo
) : TaxesUseCases.TaxesInvoke {
    override suspend fun invoke(taxesRequestModel: TaxesRequestModel):
            ApiResponse<BaseResponse<Taxes>, Error> =
        taxesRepo.getTaxes(taxesRequestModel)

}