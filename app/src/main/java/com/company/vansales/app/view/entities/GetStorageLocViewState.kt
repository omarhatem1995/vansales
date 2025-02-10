package com.company.vansales.app.view.entities

import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationResponse


sealed class GetStorageLocViewState {
    data class Loading(val show: Boolean) : GetStorageLocViewState()
    object NetworkFailure : GetStorageLocViewState()
    data class Data(val data: BaseResponse<StorageLocationResponse>) : GetStorageLocViewState()
}