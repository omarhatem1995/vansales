package com.company.vansales.app.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.*
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.localmodels.ApplicationConfig
import com.company.vansales.app.datamodel.models.localmodels.ApplicationSyncTimeStamp
import com.company.vansales.app.datamodel.models.localmodels.InvoiceNumber
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.models.mastermodels.PricesRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.models.mastermodels.TaxesRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.VisitsBody
import com.company.vansales.app.datamodel.repository.ApplicationSettingsRepository
import com.company.vansales.app.datamodel.repository.CustomerRepository
import com.company.vansales.app.datamodel.repository.GetPriceConditionsRepositoryImpl
import com.company.vansales.app.datamodel.repository.GetPricesRepositoryImpl
import com.company.vansales.app.datamodel.repository.ItemPriceConditionRepository
import com.company.vansales.app.datamodel.repository.MaterialsRepository
import com.company.vansales.app.datamodel.repository.MaterialsUnitRepository
import com.company.vansales.app.datamodel.repository.NumberRangeRepositoryImpl
import com.company.vansales.app.datamodel.repository.PricesRepository
import com.company.vansales.app.datamodel.repository.RoutesRepository
import com.company.vansales.app.datamodel.repository.SalesDocRepository
import com.company.vansales.app.datamodel.repository.TaxesRepositoryImpl
import com.company.vansales.app.datamodel.repository.TransactionsHistoryRepository
import com.company.vansales.app.datamodel.repository.TruckManagementRepository
import com.company.vansales.app.datamodel.repository.VanSalesConfigRepositoryImpl
import com.company.vansales.app.datamodel.repository.VisitsRepository
import com.company.vansales.app.datamodel.services.api.GetPriceConditionsGateway
import com.company.vansales.app.datamodel.services.api.GetPricesGateway
import com.company.vansales.app.datamodel.services.api.NumberRangeGateway
import com.company.vansales.app.datamodel.services.api.TaxesGateway
import com.company.vansales.app.datamodel.services.api.VanSalesConfigGateway
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.domain.usecases.GetPriceConditionsUseCases
import com.company.vansales.app.domain.usecases.GetPricesUseCases
import com.company.vansales.app.domain.usecases.NumberRangeUseCases
import com.company.vansales.app.domain.usecases.TaxesUseCases
import com.company.vansales.app.domain.usecases.VanSalesConfigUseCases
import com.company.vansales.app.framework.GetPriceConditionsUseCaseImpl
import com.company.vansales.app.framework.GetPricesUseCaseImpl
import com.company.vansales.app.framework.NumberRangeUseCaseImpl
import com.company.vansales.app.framework.TaxesUseCaseImpl
import com.company.vansales.app.framework.VanSalesConfigUseCaseImpl
import com.company.vansales.app.utils.Constants
import com.company.vansales.app.utils.Constants.CONDITIONS
import com.company.vansales.app.utils.Constants.CUSTOMERS
import com.company.vansales.app.utils.Constants.DELETED
import com.company.vansales.app.utils.Constants.MATERIALS
import com.company.vansales.app.utils.Constants.MATERIALS_UNIT
import com.company.vansales.app.utils.Constants.NUMBER_OF_RETRIES
import com.company.vansales.app.utils.Constants.PRICES
import com.company.vansales.app.utils.Constants.STATUS_LOADED
import com.company.vansales.app.utils.Constants.VISITS
import com.company.vansales.app.view.entities.GetPriceConditionsViewState
import com.company.vansales.app.view.entities.GetPricesViewState
import com.company.vansales.app.view.entities.NumberRangeViewState
import com.company.vansales.app.view.entities.TaxesViewState
import com.company.vansales.app.view.entities.VanSalesConfigViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class StartDayViewModel @Inject constructor(
    val numberRangeGateway: NumberRangeGateway,
    val vanSalesConfigGateway: VanSalesConfigGateway,
    val taxesGateway: TaxesGateway,
    val getPricesGateway: GetPricesGateway,
    val getPriceConditionsGateway: GetPriceConditionsGateway,
) :
    ViewModel() {

    val customerRepository: CustomerRepository = CustomerRepository(application)
    val routesRepository = RoutesRepository(application)
    val applicationSettingsRepository = ApplicationSettingsRepository(application)
    var sharedPreferences = GetSharedPreferences(application)

    private val materialRepository: MaterialsRepository = MaterialsRepository(application)
    private val materialUnitRepository: MaterialsUnitRepository =
        MaterialsUnitRepository(application)
    private val truckManagementRepository: TruckManagementRepository =
        TruckManagementRepository(application)
    private val conditionsRepository: ItemPriceConditionRepository =
        ItemPriceConditionRepository(application)
    private val pricesRepository: PricesRepository = PricesRepository(application)
    private val visitsRepository: VisitsRepository = VisitsRepository(application)
    private val transactionsHistory: TransactionsHistoryRepository =
        TransactionsHistoryRepository(application)
    private val salesDocRepository: SalesDocRepository =
        SalesDocRepository(application)

    private val myExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    val progressMLD = MutableLiveData<String>()
    var compositeDisposable = CompositeDisposable()

    //Customer Error Handling
    val customerStatusMutableLiveData = MutableLiveData<String>()
    val customerStatusLiveData: LiveData<String>
        get() = customerStatusMutableLiveData


    //Materials Error Handling
    val materialsStatusMutableLiveData = MutableLiveData<String>()
    val materialsStatusLiveData: LiveData<String>
        get() = materialsStatusMutableLiveData


    //Materials Unit Error Handling
    val materialsUnitStatusMutableLiveData = MutableLiveData<String>()
    val materialsUnitStatusLiveData: LiveData<String>
        get() = materialsUnitStatusMutableLiveData


    //Truck Content Error Handling
    val truckContentStatusMutableLiveData = MutableLiveData<String>()
    val truckContentStatusLiveData: LiveData<String>
        get() = truckContentStatusMutableLiveData

    //Truck Content Error Handling
    val visitsStatusMutableLiveData = MutableLiveData<String>()
    val visitsStatusLiveData: LiveData<String>
        get() = visitsStatusMutableLiveData


    //Prices  Error Handling
    val pricesStatusMutableLiveData = MutableLiveData<String>()
    val pricesStatusLiveData: LiveData<String>
        get() = pricesStatusMutableLiveData

    //Conditions  Error Handling
    val conditionsStatusMutableLiveData = MutableLiveData<String>()
    val conditionsStatusLiveData: LiveData<String>
        get() = conditionsStatusMutableLiveData

    fun deleteAllOldData() {
        val userPreference = GetSharedPreferences(application)
        customerRepository.customersDAO.deleteAllCustomers()
        materialRepository.materialsDAO.deleteAllMaterials()
        materialRepository.deleteAllTaxes()
        materialUnitRepository.materialsUnitDAO.deleteAllMaterialsUnit()
        truckManagementRepository.deleteAllTruckContent()
        visitsRepository.visitsDAO.deleteAllVisits()
        pricesRepository.pricesDAO.deleteAllPrices()
        conditionsRepository.itemPriceConditionDAO.deleteAllConditions()
        transactionsHistory.transactionsHistoryDAO.deleteAllTransactions()
        salesDocRepository.deleteAllSalesDocHeaderItems()
        salesDocRepository.deleteAllSalesDocHeader()
        salesDocRepository.deleteAllSalesDocBatches()
        salesDocRepository.deleteAllInvoices()
        salesDocRepository.deleteAllInvoiceHeaders()
        applicationSettingsRepository.deleteAllApplicationSyncData()
        userPreference.deleteAllSharedPref()
        userPreference.setCurrentPricesPageNo(1)
        userPreference.setCurrentPriceConditionPageNo(1)

    }

    fun deleteEndOfDayData() {
        customerRepository.customersDAO.deleteAllCustomers()
        materialRepository.materialsDAO.deleteAllMaterials()
        materialRepository.deleteAllTaxes()
        materialUnitRepository.materialsUnitDAO.deleteAllMaterialsUnit()
        truckManagementRepository.deleteAllTruckContent()
        visitsRepository.visitsDAO.deleteAllVisits()
        transactionsHistory.transactionsHistoryDAO.deleteAllTransactions()
        salesDocRepository.deleteAllSalesDocHeaderItems()
        salesDocRepository.deleteAllSalesDocHeader()
        salesDocRepository.deleteAllSalesDocBatches()
        salesDocRepository.deleteAllInvoices()
        salesDocRepository.deleteAllInvoiceHeaders()
    }

    fun deleteAllTaxes() {
        materialRepository.deleteAllTaxes()
    }

    fun triggerStartDayScenario(activity: Activity, baseBody: BaseBody, openedActivity: String) {
        if (openedActivity == Constants.START_DAY_ACTIVITY)
            deleteEndOfDayData()
        getCustomers(activity, baseBody)
    }

    fun updateCustomer(customer: Customer) {
        customerRepository.updateCustomer(customer)
    }

    fun getCustomers(activity: Activity, baseBody: BaseBody) {

        /*        if (applicationSettingsRepository.getAppTimeStampByType(CUSTOMERS) != null) {
                    baseBody.timestamp = applicationSettingsRepository.getAppTimeStampByType(CUSTOMERS).timeStamp?:""
                }*/
        val customerListDisposable =
            customerRepository.getCustomersRemote(baseBody)
                .subscribeOn(Schedulers.io())
                .retry(NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { customerResponse ->
                        myExecutor.execute {
                            customerRepository.deleteAllData()
//                            customerRepository.upsert(customerResponse.data)
//                            for(i in customerResponse.data)
                            var customerListData = ArrayList<Customer>()
                            customerResponse.data.forEach { item ->
                                if (item.operation != DELETED)
                                    customerListData.add(item)
                            }
                            customerRepository.updateCustomer(customerListData)
                            customerResponse.timeStamp?.let {
                                it
                                ApplicationSyncTimeStamp(
                                    1, CUSTOMERS,
                                    it
                                )
                            }?.let {
                                applicationSettingsRepository.insertAppTypeTimeStamp(
                                    it
                                )
                            }
                        }
                        customerRepository.getCustomersLocal.observe(
                            activity as LifecycleOwner,
                            Observer { customers ->
                                if (customers != null) {
                                    customerStatusMutableLiveData.value =
                                        STATUS_LOADED
                                    customerRepository.getCustomersLocal.removeObservers(activity)
                                }
                            })
                    },
                    {
                        customerStatusMutableLiveData.value = Constants.STATUS_ERROR
                    })
        compositeDisposable.add(customerListDisposable)
    }

    fun getMaterials(onNext: Boolean, baseBody: BaseBody, visitsBody: VisitsBody?) {
        /*        if (applicationSettingsRepository.getAppTimeStampByType(MATERIALS) != null) {
                    baseBody.timestamp = applicationSettingsRepository.getAppTimeStampByType(MATERIALS).timeStamp?:""
                }else{
                    baseBody.timestamp = null
                }*/
        val materialsListDisposable =
            materialRepository.getMaterialsRemote(baseBody)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    if (onNext) {
                        visitsBody?.let { body -> getMaterialsUnit(onNext, baseBody, body) }
                    }
                }
                .retry(Constants.NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { materialsResponse ->
                        myExecutor.execute {
                            val materialsSubLists =
                                materialsResponse.data.chunked(size = 5000)
                            materialsResponse.timeStamp?.let {
                                ApplicationSyncTimeStamp(
                                    1, MATERIALS,
                                    it
                                )
                            }?.let {
                                applicationSettingsRepository.insertAppTypeTimeStamp(
                                    it
                                )
                            }
                            for (i in materialsSubLists.indices) {
                                materialRepository.update(materialsSubLists[i])
                            }
                            materialsStatusMutableLiveData.postValue(Constants.STATUS_LOADED)
                        }
                    },
                    {
                        materialsStatusMutableLiveData.value = Constants.STATUS_ERROR
                    })
        compositeDisposable.add(materialsListDisposable)
    }


    fun getMaterialsUnit(onNext: Boolean, baseBody: BaseBody, visitsBody: VisitsBody?) {
        /*    if (applicationSettingsRepository.getAppTimeStampByType(MATERIALS_UNIT) != null) {
                baseBody.timestamp = applicationSettingsRepository.getAppTimeStampByType(MATERIALS_UNIT).timeStamp?:""
            }else{
                baseBody.timestamp = null
            }*/
        val materialsUnitListDisposable =
            materialUnitRepository.getMaterialsUnitRemote(baseBody)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    if (onNext) {
                        getTruckContent(onNext, baseBody, visitsBody)
                    }
                }
                .retry(Constants.NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { materialsUnitResponse ->
                        myExecutor.execute {
                            val materialUnitSubLists =
                                materialsUnitResponse.data.chunked(size = 5000)
                            materialsUnitResponse.timeStamp?.let {
                                ApplicationSyncTimeStamp(
                                    1, MATERIALS_UNIT,
                                    it
                                )
                            }?.let {
                                applicationSettingsRepository.insertAppTypeTimeStamp(
                                    it
                                )
                            }

                            for (i in materialUnitSubLists.indices) {
                                materialUnitRepository.update(materialUnitSubLists[i])
                            }
                            materialsUnitStatusMutableLiveData.postValue(Constants.STATUS_LOADED)
                        }
                    },
                    {
                        materialsUnitStatusMutableLiveData.value = Constants.STATUS_ERROR
                    })
        compositeDisposable.add(materialsUnitListDisposable)
    }


    fun getTruckContent(onNext: Boolean, baseBody: BaseBody, visitsBody: VisitsBody?) {
//        baseBody.timestamp = applicationSettingsRepository.getAppTimeStampByType(TRUCK_CONTENT).timeStamp?:""
        val truckContentDisposable =
            truckManagementRepository.getTruckContentRemote(baseBody)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    if (onNext) {
                        getVisits(onNext, baseBody, visitsBody!!)
                    }
                }
                .retry(Constants.NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { truckContent ->
                        truckManagementRepository.deleteAllTruckContent()
                        myExecutor.execute {
                            truckManagementRepository.upsertTruckItems(
                                truckContent.data.truckItem,
                                true
                            )
                            /*               applicationSettingsRepository.insertAppTypeTimeStamp(
                                               ApplicationSyncTimeStamp(1,TRUCK_CONTENT,truckContent.timeStamp) )*/

                            truckManagementRepository.upsertTruckBatches(truckContent.data.truckBatch)
                            truckContentStatusMutableLiveData.postValue(Constants.STATUS_LOADED)

                        }
                    },
                    {
                        truckContentStatusMutableLiveData.value = Constants.STATUS_ERROR
                    })
        compositeDisposable.add(truckContentDisposable)
    }


    fun getVisits(onNext: Boolean, baseBody: BaseBody, visitsBody: VisitsBody?) {
        if (applicationSettingsRepository.getAppTimeStampByType(VISITS) != null) {
            baseBody.timestamp =
                applicationSettingsRepository.getAppTimeStampByType(VISITS).timeStamp ?: ""
        } else {
            baseBody.timestamp = null
        }
        visitsBody?.let {
            val visitsDisposable =
                visitsRepository.getVisitsRemote(visitsBody)
                    .subscribeOn(Schedulers.io())
                    .doOnNext {
                        if (onNext) {
                            getPrices(
                                onNext, baseBody
                            )
//                            getPrices(onNext, baseBody)
                        }
                    }
                    .retry(Constants.NUMBER_OF_RETRIES)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { visits ->
                            myExecutor.execute {
                                visitsRepository.deleteAllData()
                                visitsRepository.update(visits.data)
                                visitsStatusMutableLiveData.postValue(Constants.STATUS_LOADED)
                                visits.timeStamp?.let { it1 ->
                                    ApplicationSyncTimeStamp(
                                        1, VISITS,
                                        it1
                                    )
                                }?.let { it2 ->
                                    applicationSettingsRepository.insertAppTypeTimeStamp(
                                        it2
                                    )
                                }
                            }
                        },
                        {
                            visitsStatusMutableLiveData.value = Constants.STATUS_ERROR
                        })
            compositeDisposable.add(visitsDisposable)
        }
    }

    /*fun getPrices(onNext: Boolean, baseBody: BaseBody) {
*//*        if (applicationSettingsRepository.getAppTimeStampByType(PRICES) != null) {
            baseBody.timestamp = applicationSettingsRepository.getAppTimeStampByType(PRICES).timeStamp?:""
        }else{
            baseBody.timestamp = null
        }*//*
        val pricesDisposable =
            pricesRepository.getPricesRemotely(baseBody)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    if (onNext) {
                        getConditions(baseBody)
                    }
                }
                .retry(Constants.NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { prices ->
                        myExecutor.execute {
                            val pricesSubLists = prices.data.chunked(size = 5000)
                            prices.timeStamp?.let { ApplicationSyncTimeStamp(1, PRICES, it) }?.let {
                                applicationSettingsRepository.insertAppTypeTimeStamp(
                                    it
                                )
                            }
                            for (i in pricesSubLists.indices) {
//                                pricesRepository.upsert(pricesSubLists[i])
                                pricesRepository.update(pricesSubLists[i])
                            }
                            pricesStatusMutableLiveData.postValue(Constants.STATUS_LOADED)
                        }
                    }, {
                        pricesStatusMutableLiveData.value = Constants.STATUS_ERROR
                    })
        compositeDisposable.add(pricesDisposable)
    }*/
    fun getConditions(baseBody: BaseBody) = viewModelScope.launch {
        val currentPageNo = sharedPreferences.getCurrentPriceConditionPageNo()
        val pricesRequestModel = PricesRequestModel(baseBody.driver,baseBody.distChannel
            ,baseBody.salesOrg,currentPageNo.toString(),"12000","")
        if (applicationSettingsRepository.getAppTimeStampByType(CONDITIONS) != null) {
            pricesRequestModel.changeTimestamp =
                applicationSettingsRepository.getAppTimeStampByType(CONDITIONS).timeStamp
        } else {
            pricesRequestModel.changeTimestamp = null
        }
        viewStateConditions.postValue(GetPriceConditionsViewState.Loading(true))

        var timeStamp = ""
        var totalItems : Int = if(currentPageNo == 1)
            0
        else
            (currentPageNo * 12000) - 12000
        var pageNo = currentPageNo
        withContext(Dispatchers.Default) {
            while (true) {
                val requestModel =
                    pricesRequestModel.copy(pageNo = pageNo.toString(), pageSize = "12000")
                when (val response = priceConditionsUseCase.invoke(requestModel)) {
                    is ApiResponse.Success -> {
                        val pageData = response.body.data
                        val allData = mutableListOf<ItemPriceCondition>()
                        val allDataToDelete = mutableListOf<ItemPriceCondition>()

                        pageData.forEach { item ->
                            if (item.operation == "D")
                                allDataToDelete.add(item)
                            else
                                allData.add(item)
                        }

                        val pageItems = pageData.size
                        totalItems += pageItems

                        timeStamp = "${response.body.timeStamp}"

                        val loadingMessage = "$totalItems items"
                        viewStateConditions.postValue(
                            GetPriceConditionsViewState.LoadingMessage(
                                loadingMessage
                            )
                        )

                        if (pageData.isEmpty()) {
                            sharedPreferences.setCurrentPriceConditionPageNo(1)
                            break
                        }

                        sharedPreferences.setCurrentPriceConditionPageNo(pageNo)
                        withContext(Dispatchers.IO) {
                            conditionsRepository.update(allData)
                            conditionsRepository.deleteItems(allDataToDelete)
                        }
                    }
                    is ApiResponse.NetworkError -> {
//                    retryCountConditions++
//                    if (retryCountConditions >= 5) { // If 5 retries have failed, emit an error message
                        viewStateConditions.postValue(GetPriceConditionsViewState.NetworkFailure)
                        conditionsStatusMutableLiveData.postValue(Constants.STATUS_ERROR)
//                    } else {
//                        retryPriceConditions(pricesRequestModel)
//                    }
                        return@withContext
                    }
                    else -> {
                        conditionsStatusMutableLiveData.postValue(Constants.STATUS_ERROR)
                        viewStateConditions.postValue(GetPriceConditionsViewState.Loading(false))
                        return@withContext
                    }
                }

                pageNo++
            }
            viewStateConditions.postValue(GetPriceConditionsViewState.Loading(false))

            withContext(Dispatchers.IO) {
                applicationSettingsRepository.insertAppTypeTimeStamp(
                    ApplicationSyncTimeStamp(
                        1,
                        CONDITIONS,
                        timeStamp
                    )
                )
            }

            conditionsStatusMutableLiveData.postValue(Constants.STATUS_LOADED)
        }
    }
    /*fun getConditions(baseBody: BaseBody) {
        if (applicationSettingsRepository.getAppTimeStampByType(CONDITIONS) != null) {
            baseBody.timestamp =
                applicationSettingsRepository.getAppTimeStampByType(CONDITIONS).timeStamp ?: ""
        }
        val conditionsDisposable =
            conditionsRepository.getItemPriceConditionRemotely(baseBody)
                .subscribeOn(Schedulers.io())
                .retry(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { conditions ->
                        myExecutor.execute {
                            val conditionsSubLists = conditions.data.chunked(size = 5000)
                            for (i in conditionsSubLists.indices) {
                                progressMLD.postValue(i.toString() + "," + conditionsSubLists.size.toString())
//                                conditionsRepository.upsert(conditionsSubLists[i])
                                conditionsRepository.update(conditionsSubLists[i])
                                conditions.timeStamp?.let {
                                    ApplicationSyncTimeStamp(
                                        1, CONDITIONS,
                                        it
                                    )
                                }?.let {
                                    applicationSettingsRepository.insertAppTypeTimeStamp(
                                        it
                                    )
                                }

                                *//*           if(conditionsSubLists[i][i].operation == "D"){
                                               conditionsRepository.deleteItem(conditionsSubLists[i][i])
                                           }*//*
                            }
                            conditionsStatusMutableLiveData.postValue(Constants.STATUS_LOADED)
                        }
                    },
                    {
                        conditionsStatusMutableLiveData.value = Constants.STATUS_ERROR
                    })
        compositeDisposable.add(conditionsDisposable)
    }*/

    val viewStateNumberRange: MutableLiveData<NumberRangeViewState> = MutableLiveData()
    private val numberRangeUseCase = createNumberRangeUserCase()

    val viewStateVanSalesConfig: MutableLiveData<VanSalesConfigViewState> = MutableLiveData()
    private val vanSalesConfigUseCase = createVanSalesConfigUserCase()

    val viewStateTaxes: MutableLiveData<TaxesViewState> = MutableLiveData()
    private val taxesUseCase = createTaxesUserCase()

    val viewStatePrices: MutableLiveData<GetPricesViewState> = MutableLiveData()
    private val pricesUseCase = createPricesUseCase()

    val viewStateConditions: MutableLiveData<GetPriceConditionsViewState> = MutableLiveData()
    private val priceConditionsUseCase = createPriceConditionsUseCase()

    fun getNumberRange(numberRangeRequestModel: NumberRangeRequestModel) = viewModelScope.launch {
        viewStateNumberRange.postValue(NumberRangeViewState.Loading(true))
        when (val response = numberRangeUseCase.invoke(numberRangeRequestModel)) {
            is ApiResponse.Success -> {
                viewStateNumberRange.postValue(
                    NumberRangeViewState.Data(response.body.data)
                )
            }
            is ApiResponse.NetworkError -> viewStateNumberRange.postValue(
                NumberRangeViewState.NetworkFailure
            )
            else -> viewStateNumberRange.postValue(NumberRangeViewState.Loading(false))
        }
    }

    fun getTaxes(taxesRequestModel: TaxesRequestModel) = viewModelScope.launch {
        viewStateTaxes.postValue(TaxesViewState.Loading(true))
        when (val response = taxesUseCase.invoke(taxesRequestModel)) {
            is ApiResponse.Success -> {
                viewStateTaxes.postValue(
                    TaxesViewState.Data(response.body.data)
                )
                if (response.body.data.isNotEmpty())
                    upsertTaxes(response.body.data)
            }
            is ApiResponse.NetworkError -> viewStateTaxes.postValue(
                TaxesViewState.NetworkFailure
            )
            else -> viewStateTaxes.postValue(TaxesViewState.Loading(false))
        }
    }

    var retryCountPrices = 0
    var retryCountConditions = 0
    fun retryPrices(onNext: Boolean, baseBody: BaseBody) {
        getPrices(onNext, baseBody)
    }

    fun retryPriceConditions(baseBody: BaseBody) {
        getConditions(baseBody)
    }

    fun getPrices(onNext: Boolean, baseBody: BaseBody) =
        viewModelScope.launch {
            val currentPageNo = sharedPreferences.getCurrentPricesPageNo()
            Log.d("pageNoEquals", "current Page No $currentPageNo")
            val pricesRequestModel = PricesRequestModel(baseBody.driver,baseBody.distChannel
                ,baseBody.salesOrg,currentPageNo.toString(),"12000","")
            if (applicationSettingsRepository.getAppTimeStampByType(PRICES) != null) {
                pricesRequestModel.changeTimestamp =
                    applicationSettingsRepository.getAppTimeStampByType(PRICES).timeStamp
                Log.d("getCurrentTypeStampPrice", " ${applicationSettingsRepository.getAppTimeStampByType(PRICES)}")
            } else {
                pricesRequestModel.changeTimestamp = null
            }
            viewStatePrices.postValue(GetPricesViewState.Loading(true))

            var timeStamp = ""
            var totalItems : Int = if(currentPageNo == 1)
                0
            else
                (currentPageNo * 12000) - 12000
            var pageNo = currentPageNo
            withContext(Dispatchers.Default) {
                while (true) {
                    val requestModel =
                        pricesRequestModel.copy(pageNo = pageNo.toString(), pageSize = "12000")
                    Log.d("pageNoEquals", "$pageNo")

                    when (val response = pricesUseCase.invoke(requestModel)) {
                        is ApiResponse.Success -> {
                            val pageData = response.body.data

                            val allData = mutableListOf<Prices>()
                            val allDataToDelete = mutableListOf<Prices>()
                            pageData.forEach { item ->
                                if (item.operation == Constants.DELETED)
                                    allDataToDelete.add(item)
                                else
                                    allData.add(item)
                            }
                            sharedPreferences.setCurrentPricesPageNo(pageNo)
                            timeStamp = "${response.body.timeStamp}"
                            withContext(Dispatchers.IO) {
                                pricesRepository.update(allData)
                                pricesRepository.deleteItems(allDataToDelete)
                            }
                            val pageItems = pageData.size
                            totalItems += pageItems
                            val loadingMessage = "Loaded $totalItems items"
                            viewStatePrices.postValue(
                                GetPricesViewState.LoadingMessage(
                                    loadingMessage
                                )
                            )

                            if (pageData.isEmpty()) {
                                sharedPreferences.setCurrentPricesPageNo(1)
                                Log.d("pageNoEquals", "current Page No in Break $currentPageNo")
                                break
                            }
                        }
                        is ApiResponse.NetworkError -> {
//                        retryCountPrices++
//                        if (retryCountPrices >= 5) { // If 5 retries have failed, emit an error message
                            viewStatePrices.postValue(GetPricesViewState.NetworkFailure)
                            pricesStatusMutableLiveData.postValue(Constants.STATUS_ERROR)

//                        } else {
//                            retryPrices(onNext, baseBody, pricesRequestModel)
//                        }
                            return@withContext
                        }
                        else -> {
                            pricesStatusMutableLiveData.postValue(Constants.STATUS_ERROR)
                            viewStatePrices.postValue(GetPricesViewState.Loading(false))
                            return@withContext
                        }
                    }

                    pageNo++
                }

                viewStatePrices.postValue(GetPricesViewState.Loading(false))

                withContext(Dispatchers.IO) {
                    applicationSettingsRepository.insertAppTypeTimeStamp(
                        ApplicationSyncTimeStamp(
                            1,
                            PRICES, timeStamp
                        )
                    )
                }
                withContext(Dispatchers.Main) {
                    if (onNext) {
                        getConditions(baseBody)
                    }
                }

                pricesStatusMutableLiveData.postValue(Constants.STATUS_LOADED)
            }

        }

    fun getVanSalesConfig(vanSalesConfigRequestModel: VanSalesConfigRequestModel) =
        viewModelScope.launch {
            viewStateVanSalesConfig.postValue(VanSalesConfigViewState.Loading(true))
            when (val response = vanSalesConfigUseCase.invoke(vanSalesConfigRequestModel)) {
                is ApiResponse.Success -> {
                    viewStateVanSalesConfig.postValue(
                        VanSalesConfigViewState.Data(response.body.data)
                    )
                }
                is ApiResponse.NetworkError -> viewStateVanSalesConfig.postValue(
                    VanSalesConfigViewState.NetworkFailure
                )
                else -> viewStateVanSalesConfig.postValue(VanSalesConfigViewState.Loading(false))
            }
        }

    fun upsert(invoiceNumber: InvoiceNumber) {
        routesRepository.upsert(invoiceNumber)
    }

    fun upsertAppSettings(applicationConfig: List<ApplicationConfig>) {
        applicationSettingsRepository.upsertApplicationSettings(applicationConfig)
    }

    suspend fun upsertTaxes(taxes: List<Taxes>) {
        Log.d("upsertTaxes", " ${taxes.size}")
        taxesRepo.upsertTaxes(taxes)
    }

    var taxesRepo = TaxesRepositoryImpl(application, taxesGateway)
    private fun createNumberRangeUserCase(): NumberRangeUseCases.NumberRangeInvoke {
        return NumberRangeUseCaseImpl(NumberRangeRepositoryImpl(numberRangeGateway))
    }

    private fun createVanSalesConfigUserCase(): VanSalesConfigUseCases.VanSalesConfigInvoke {
        return VanSalesConfigUseCaseImpl(VanSalesConfigRepositoryImpl(vanSalesConfigGateway))
    }

    private fun createTaxesUserCase(): TaxesUseCases.TaxesInvoke {
        return TaxesUseCaseImpl(TaxesRepositoryImpl(application, taxesGateway))
    }

    private fun createPricesUseCase(): GetPricesUseCases.GetPricesInvoke {
        return GetPricesUseCaseImpl(GetPricesRepositoryImpl(getPricesGateway))
    }

    private fun createPriceConditionsUseCase(): GetPriceConditionsUseCases.GetPriceConditionsInvoke {
        Log.d("getConditions", " Use case is called ")

        return GetPriceConditionsUseCaseImpl(
            GetPriceConditionsRepositoryImpl(
                getPriceConditionsGateway
            )
        )
    }
}