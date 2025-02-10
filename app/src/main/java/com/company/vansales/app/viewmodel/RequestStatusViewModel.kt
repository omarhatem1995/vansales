package com.company.vansales.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.models.mastermodels.RequestStatusRequestModel
import com.company.vansales.app.datamodel.repository.MaterialsRepository
import com.company.vansales.app.datamodel.repository.RequestStatusRepositoryImpl
import com.company.vansales.app.datamodel.services.api.RequestStatusGateway
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.domain.usecases.RequestStatusUseCases
import com.company.vansales.app.framework.RequestStatusUseCaseImpl
import com.company.vansales.app.view.entities.RequestStatusViewState
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestStatusViewModel @Inject constructor(
    val requestStatusGateway: RequestStatusGateway
) :
ViewModel(){
    var sharedPref = GetSharedPreferences(application)
    private val mRepository = MaterialsRepository(application)

    val viewStateRequestStatus: MutableLiveData<RequestStatusViewState> = MutableLiveData()
    private val requestStatusUseCase = createRequestStatusUseCase()

    fun getMaterialNameByMaterialNo(materialNo: String): Materials {
        return mRepository.getMaterialByMaterialNo(materialNo)
    }
    fun requestStatus(requestStatusRequestModel: RequestStatusRequestModel) = viewModelScope.launch {
        viewStateRequestStatus.postValue(RequestStatusViewState.Loading(true))
        when (val response = requestStatusUseCase.invoke(requestStatusRequestModel)) {
            is ApiResponse.Success -> {
                viewStateRequestStatus.postValue(
                    RequestStatusViewState.Data(response.body)
                )
            }
            is ApiResponse.NetworkError -> viewStateRequestStatus.postValue(
                RequestStatusViewState.NetworkFailure
            )
            else -> viewStateRequestStatus.postValue(RequestStatusViewState.Loading(false))
        }
    }

    private fun createRequestStatusUseCase(): RequestStatusUseCases.RequestStatusInvoke {
        return RequestStatusUseCaseImpl(RequestStatusRepositoryImpl(requestStatusGateway))
    }
}