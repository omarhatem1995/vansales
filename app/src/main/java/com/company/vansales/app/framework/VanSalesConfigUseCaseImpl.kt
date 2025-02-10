package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.domain.repos.ApprovalsStatusRepo
import com.company.vansales.app.domain.repos.BillToBillCheckRepo
import com.company.vansales.app.domain.repos.CreateCustomerRepo
import com.company.vansales.app.domain.repos.VanSalesConfigRepo
import com.company.vansales.app.domain.usecases.ApprovalsStatusUseCases
import com.company.vansales.app.domain.usecases.BillToBillCheckUseCases
import com.company.vansales.app.domain.usecases.CreateCustomerUseCases
import com.company.vansales.app.domain.usecases.VanSalesConfigUseCases

class VanSalesConfigUseCaseImpl (
    val vanSalesConfigRepo: VanSalesConfigRepo
) : VanSalesConfigUseCases.VanSalesConfigInvoke {
    override suspend fun invoke(vanSalesConfigRequestModel: VanSalesConfigRequestModel):
            ApiResponse<BaseResponse<VanSalesConfigResponseModel>, Error> =
                vanSalesConfigRepo.vanSalesConfig(vanSalesConfigRequestModel)

}