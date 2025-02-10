package com.company.vansales.app.datamodel.services.api

import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.HelpView
import retrofit2.http.Body
import retrofit2.http.POST

interface GetHelpGateway {

    @POST("HelpView/find")
    suspend fun getHelpView(@Body baseBody: BaseBody):
            ApiResponse<BaseResponse<HelpView>, Error>

}