package com.company.vansales.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.localmodels.ApplicationSettings
import com.company.vansales.app.datamodel.models.mastermodels.DriverDataRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginRequestModel
import com.company.vansales.app.datamodel.repository.ApplicationSettingsRepository
import com.company.vansales.app.datamodel.repository.DriverDataRepositoryImpl
import com.company.vansales.app.datamodel.repository.LoginRepositoryImpl
import com.company.vansales.app.datamodel.services.api.DriverDataGateway
import com.company.vansales.app.datamodel.services.api.LoginGateway
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.domain.usecases.DriverDataUseCases
import com.company.vansales.app.domain.usecases.LoginUseCases
import com.company.vansales.app.framework.DriverDataUseCaseImpl
import com.company.vansales.app.framework.LoginUseCaseImpl
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.view.entities.DriverDataViewState
import com.company.vansales.app.view.entities.LoginViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import javax.inject.Inject

@HiltViewModel
class ApplicationSettingsViewModel @Inject constructor(
    val loginGateway: LoginGateway,
    val driverDataGateway: DriverDataGateway,
) : ViewModel() {

    val viewStateLogin: MutableLiveData<LoginViewState> = MutableLiveData()
    private val loginUseCase = createLoginUseCase()

    private val mRepository: ApplicationSettingsRepository =
        ApplicationSettingsRepository(application)
    lateinit var currentCustomerForUpdate : DriverDataRequestModel

    var sharedPreferences = GetSharedPreferences(application)

    val getAppSettingsLiveData: LiveData<ApplicationSettings> = mRepository.getApplicationSettingsLiveData

    val viewStateDriverData: MutableLiveData<DriverDataViewState> = MutableLiveData()
    private val driverDataUseCase = createDriverDataUseCase()
    private val _driverStateDriverDataLoaded = MutableLiveData<Boolean>()
    fun getCurrentCustomer(driverDataRequestModel: DriverDataRequestModel){
        currentCustomerForUpdate = driverDataRequestModel
    }
    fun login(admin:Boolean,loginRequestModel: LoginRequestModel) = viewModelScope.launch {
        viewStateLogin.postValue(LoginViewState.Loading(true))
        when (val response = loginUseCase.invoke(loginRequestModel)) {
            is ApiResponse.Success -> {
                viewStateLogin.postValue(
                    LoginViewState.LoginSuccess(response.body)
                )
                if (response.body.data == "S" && !admin) {
                    sharedPreferences.setUserLoggedIn(true)
                } else {
                    sharedPreferences.setUserLoggedIn(false)
                }
            }
            is ApiResponse.NetworkError -> viewStateLogin.postValue(
                LoginViewState.NetworkFailure
            )
            else -> viewStateLogin.postValue(LoginViewState.Loading(false))
        }
    }

    fun driverData(driverDataRequestModel: DriverDataRequestModel) = viewModelScope.launch {
        viewStateDriverData.postValue(DriverDataViewState.Loading(true))
        Log.d("callAPIinApp" , "DriverData*(")
        when (val response = driverDataUseCase.invoke(driverDataRequestModel)) {
            is ApiResponse.Success -> {
                _driverStateDriverDataLoaded.value = true
                viewStateDriverData.postValue(
                    DriverDataViewState.DriverDataSuccess(response.body)
                )
            }
            is ApiResponse.NetworkError -> viewStateDriverData.postValue(
                DriverDataViewState.NetworkFailure
            )
            else -> viewStateDriverData.postValue(DriverDataViewState.Loading(false))
        }
    }
    fun updateSettings(port: String, host: String, driver: String?,printerMACAddress : String?) {
        mRepository.updateSettings(port, host, driver,printerMACAddress)
    }

    fun updateUrl() {
        RetrofitUrlManager.getInstance().setGlobalDomain(AppUtils.urlBuilder(application))
    }

    fun insert(applicationSettings: ApplicationSettings) {
        mRepository.insert(applicationSettings)
    }

    fun updateDayStatus(isDayStarted: Boolean) {
        mRepository.updateDayStatus(isDayStarted)
    }

    private fun createDriverDataUseCase(): DriverDataUseCases.DriverDataInvoke {
        return DriverDataUseCaseImpl(DriverDataRepositoryImpl(driverDataGateway))
    }
    private fun createLoginUseCase(): LoginUseCases.LoginInvoke {
        return LoginUseCaseImpl(LoginRepositoryImpl(loginGateway))
    }
}