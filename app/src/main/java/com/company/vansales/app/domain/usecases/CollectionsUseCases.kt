package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface CollectionsUseCases {
    interface View {
        fun renderCollections(data: BaseResponse<CollectionsResponseModel>)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface GetCollectionsInvoke {
        suspend fun invoke(collectionsRequestModel: CollectionsRequestModel) :
                ApiResponse<BaseResponse<CollectionsResponseModel>, Error>
    }
}