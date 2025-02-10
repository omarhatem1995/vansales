package com.company.vansales.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.HelpView
import com.company.vansales.app.datamodel.repository.GetHelpRepositoryImpl
import com.company.vansales.app.datamodel.repository.HelpViewRepository
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.services.api.GetHelpGateway
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.domain.usecases.GetHelpUseCases
import com.company.vansales.app.framework.GetHelpUseCaseImpl
import com.company.vansales.app.utils.Constants
import com.company.vansales.app.view.entities.GetHelpViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HelpViewViewModel@Inject constructor(
    private val getGetHelpGateway: GetHelpGateway
    ) : ViewModel() {

    val viewStateHelpView: MutableLiveData<GetHelpViewState> = MutableLiveData()
    private val createHelpUseCase = createHelpUseCase()
    val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    var sharedPreferences = GetSharedPreferences(application)
    private val mRepository: HelpViewRepository = HelpViewRepository()

    fun getHelp(baseBody: BaseBody) = viewModelScope.launch {
        viewStateHelpView.postValue(GetHelpViewState.Loading(true))
        when (val response = createHelpUseCase.invoke(baseBody)) {
            is ApiResponse.Success -> {
                val listData : List<HelpView> = response.body.data
                val finalList = listData.filter { p ->
                    p.fieldLanguage == Constants.CURRENT_LANG
                            && p.fieldName == Constants.CHECK_LIST
                }
                val data = response.body
                viewStateHelpView.postValue(GetHelpViewState.Loading(false))
                viewStateHelpView.postValue(GetHelpViewState.FilteredDataByLanguage(finalList))
                mRepository.upsert(data.data)

            }
            is ApiResponse.NetworkError -> viewStateHelpView.postValue(
                GetHelpViewState.NetworkFailure
            )
            else -> viewStateHelpView.postValue(GetHelpViewState.Loading(false))
        }
    }

    private fun createHelpUseCase() : GetHelpUseCases.GetHelpInvoke {
        return GetHelpUseCaseImpl(GetHelpRepositoryImpl(getGetHelpGateway))
    }

    fun getFocReason() : List<HelpView>{
        return mRepository.getFocReason()
    }
    fun getReturnReason() : List<HelpView>{
        return mRepository.getReturnReason()
    }
/*

    var compositeDisposable = CompositeDisposable()

    private val helpViewStatusMutableLiveData = MutableLiveData<String>()
    val helpViewStatusLiveData: LiveData<String>
        get() = helpViewStatusMutableLiveData

*/

    fun getDamageCheckList(): LiveData<List<HelpView>> {
        return if (Constants.CURRENT_LANG == Constants.ENGLISH_LANGUAGE) {
            mRepository.getDamageCheckListEnglish
        } else {
            mRepository.getDamageCheckListArabic
        }
    }

/*
    fun getHelpViewRemote(baseBody: BaseBody) {
        val helpViewDisposable =
            mRepository.getHelpViewRemote(baseBody)
                .subscribeOn(Schedulers.io())
                .retry(Constants.NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())?.subscribe(
                    { helpViewList ->
                        if (!helpViewList?.data.isNullOrEmpty()) {
                            val myExecutor = Executors.newSingleThreadExecutor()
                            myExecutor.execute {
                                mRepository.upsert(helpViewList?.data)
                            }
                            helpViewStatusMutableLiveData.value = Constants.STATUS_LOADED

                        } else {
                            helpViewStatusMutableLiveData.value = Constants.STATUS_ERROR

                        }
                    },
                    {
                        helpViewStatusMutableLiveData.value = Constants.STATUS_ERROR
                    })
        compositeDisposable.add(helpViewDisposable!!)

    }
*/

}