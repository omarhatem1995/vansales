package com.company.vansales.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.repository.ApplicationSettingsRepository
import com.company.vansales.app.datamodel.repository.BillToBillCheckRepositoryImpl
import com.company.vansales.app.datamodel.repository.GetTruckContentRepositoryImpl
import com.company.vansales.app.datamodel.repository.RoutesRepository
import com.company.vansales.app.datamodel.repository.TruckManagementRepository
import com.company.vansales.app.datamodel.repository.VisitsRepository
import com.company.vansales.app.datamodel.services.api.BillToBillCheckGateway
import com.company.vansales.app.datamodel.services.api.GetTruckContentsGateway
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.domain.usecases.BillToBillCheckUseCases
import com.company.vansales.app.domain.usecases.GetTruckContentsUseCases
import com.company.vansales.app.framework.BillToBillCheckUseCaseImpl
import com.company.vansales.app.framework.GetTruckContentsUseCaseImpl
import com.company.vansales.app.view.entities.BillToBillCheckViewState
import com.company.vansales.app.view.entities.GetTruckContentsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitsViewModel @Inject constructor(
    val billToBillCheckGateway: BillToBillCheckGateway,
    val truckContentsGateway: GetTruckContentsGateway
) : ViewModel() {

    var sharedPreference = GetSharedPreferences(application)
    private val mRepository: VisitsRepository = VisitsRepository(application)
    private val routesRepository: RoutesRepository = RoutesRepository(application)
    private val getAllVisits: List<Visits> = mRepository.getAllVisits
    private val applicationConfig = ApplicationSettingsRepository(application)
    private val truckRepository = TruckManagementRepository(application)

    fun getAllVisitsByRouteAscending(route: String): LiveData<List<Visits>> {
        return mRepository.getAllVisitsByRouteAscending(route)
    }

    fun getAllVisitsByRouteDescending(route: String): LiveData<List<Visits>> {
        return mRepository.getAllVisitsByRouteDescending(route)
    }

    fun getFinishedVisits(): List<Visits> {
        return mRepository.getFinishedVisits()
    }

    fun getAllVisits(): List<Visits> {
        return mRepository.getAllFinishedVisits()
    }

    fun citiesList(): ArrayList<String> {
        return mRepository.citiesList()
    }


    fun regionsList(): ArrayList<String> {
        return mRepository.regionsList()
    }

    fun getCurrentActiveVisit(): Visits? {
        return mRepository.getCurrentActiveVisit()
    }

    fun getCurrentActiveVisitLiveData(): LiveData<Visits> {
        return mRepository.getCurrentActiveVisitLiveData()
    }

    fun updateStartVisitData(visitListId : String,visitItemNo : Int,
                                       startDate:String,startTime:String,startLatitude:Double , startLongitude:Double){
        return mRepository.updateStartVisitData(visitListId,visitItemNo,startDate,startTime,startLatitude,startLongitude)
    }
    fun updateEndVisitData(visitListId : String,visitItemNo : Int,
                                       endDate:String,endTime:String,endLatitude:Double , endLongitude:Double,failedReason:String?){
        return mRepository.updateEndVisitData(visitListId,visitItemNo,endDate,endTime,endLatitude,endLongitude,failedReason)
    }

    fun onRouteChange(
        application: Application,
        route: String,
        customerNo: String,
        salesOrg: String,
        distChannel: String
    ) {
        mRepository.onRouteChange(application, route, customerNo, salesOrg, distChannel)
    }

    fun getRoutesList(): List<String> {
        return routesRepository.getRoutesListString().distinct()
    }

    val viewStateBillToBillCheck: MutableLiveData<BillToBillCheckViewState> = MutableLiveData()
    private val createBillToBillUseCase = createBillToBillCheckUseCase()

    val viewStateTruckContents: MutableLiveData<GetTruckContentsViewState> = MutableLiveData()
    private val createTruckContentsUseCase = createTruckContentsUseCase()

    fun checkBillToBill(billToBillCheckRequestModel: BillToBillCheckRequestModel) =
        viewModelScope.launch {
            viewStateBillToBillCheck.postValue(BillToBillCheckViewState.Loading(true))
            when (val response = createBillToBillUseCase.invoke(billToBillCheckRequestModel)) {
                is ApiResponse.Success -> {
                    viewStateBillToBillCheck.postValue(
                        BillToBillCheckViewState.Data(response.body)
                    )
                }
                is ApiResponse.NetworkError -> viewStateBillToBillCheck.postValue(
                    BillToBillCheckViewState.NetworkFailure
                )
                else -> viewStateBillToBillCheck.postValue(BillToBillCheckViewState.Loading(false))
            }
        }

    fun getTruckContents() =
        viewModelScope.launch {
            val baseBody = BaseBody(sharedPreference.getSalesOrg()?:"",
            sharedPreference.getDistChannel() ?:"",sharedPreference.getDriverNumber()?:"",
            sharedPreference.getDriverNumber() ?:"")
            viewStateTruckContents.postValue(GetTruckContentsViewState.Loading(true))
            when (val response = createTruckContentsUseCase.invoke(baseBody)) {
                is ApiResponse.Success -> {
                    viewStateTruckContents.postValue(
                        GetTruckContentsViewState.Data(response.body)
                    )
                    truckRepository.deleteAllTruckContent()
                    truckRepository.upsertTruckItems(
                        response.body.data.truckItem,
                        true
                    )
                    truckRepository.upsertTruckBatches(response.body.data.truckBatch)
                }
                is ApiResponse.NetworkError -> viewStateTruckContents.postValue(
                    GetTruckContentsViewState.NetworkFailure
                )
                else -> viewStateTruckContents.postValue(GetTruckContentsViewState.Loading(false))
            }
        }

    fun getAppConfig() : String {
       return  applicationConfig.getAppConfigValueByAppParamter("NUMBER_OF_INVOICE")
    }

    private fun createBillToBillCheckUseCase(): BillToBillCheckUseCases.BillToBillCheckInvoke {
        return BillToBillCheckUseCaseImpl(BillToBillCheckRepositoryImpl(billToBillCheckGateway))
    }

    private fun createTruckContentsUseCase(): GetTruckContentsUseCases.GetTruckContentsInvoke {
        return GetTruckContentsUseCaseImpl(GetTruckContentRepositoryImpl(truckContentsGateway))
    }
}