package com.company.vansales.app.framework

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.HelpView
import com.company.vansales.app.domain.repo.HelpRepo
import com.company.vansales.app.domain.usecases.GetHelpUseCases

class GetHelpUseCaseImpl (
    val helpRepo: HelpRepo
) : GetHelpUseCases.GetHelpInvoke {
    override suspend fun invoke(baseBody: BaseBody):
            ApiResponse<BaseResponse<HelpView>, Error> =
                helpRepo.getHelp(baseBody)
}