package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationResponse
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationRequestBody
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface StorageLocationUseCases {

    interface View {
        fun renderStorageLocSuccess(data: MutableList<StorageLocationResponse>)
        fun renderStorageLocFail()
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface StorageLocInvoke {
        suspend fun invoke(storageLocationRequestBody: StorageLocationRequestBody) :
                ApiResponse<BaseResponse<StorageLocationResponse>, Error>
    }
}