package com.company.vansales.app.domain.repo

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.HelpView

interface HelpRepo {
    suspend fun getHelp(baseBody : BaseBody):
            ApiResponse<BaseResponse<HelpView>, Error>

}