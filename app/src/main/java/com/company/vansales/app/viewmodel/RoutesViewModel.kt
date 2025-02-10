package com.company.vansales.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.datamodel.repository.GetRoutesRepositoryImpl
import com.company.vansales.app.view.entities.GetRoutesViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(
    private val getRoutesRepository: GetRoutesRepositoryImpl
) : ViewModel() {

    val viewStateRoutesView: MutableLiveData<GetRoutesViewState> = MutableLiveData()

    fun getRoutes(baseBody: BaseBody) = viewModelScope.launch {
        when (val response = getRoutesRepository.getRoutes(baseBody)) {
            is ApiResponse.Success -> {
                viewStateRoutesView.postValue(
                    GetRoutesViewState.RoutesData(response.body.data)
                )
            }
            is ApiResponse.NetworkError -> viewStateRoutesView.postValue(
                GetRoutesViewState.NetworkFailure
            )
            else -> viewStateRoutesView.postValue(GetRoutesViewState.Loading(false))
        }
    }

    fun upsert(routes: List<Routes>) {
        getRoutesRepository.upsert(routes)
    }
}