package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.HelpView
import com.company.vansales.app.datamodel.services.api.GetHelpGateway
import com.company.vansales.app.domain.repo.HelpRepo

class GetHelpRepositoryImpl (
    private val getHelpGateway: GetHelpGateway
    ) : HelpRepo {

    override suspend fun getHelp(baseBody: BaseBody):
            ApiResponse<BaseResponse<HelpView>, Error> {
        return getHelpGateway.getHelpView(baseBody)
    }

}