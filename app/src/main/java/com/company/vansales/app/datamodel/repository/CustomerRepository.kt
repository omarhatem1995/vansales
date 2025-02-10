package com.company.vansales.app.datamodel.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.ApplicationSettingsDAO
import com.company.vansales.app.datamodel.room.CustomersDAO
import com.company.vansales.app.datamodel.room.RoutesDAO
import com.company.vansales.app.datamodel.services.ClientService
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.getFormatForEndDay
import com.company.vansales.app.utils.Constants.DELETED
import com.company.vansales.app.utils.Constants.IN_PROGRESS_VISIT
import com.company.vansales.app.utils.Constants.UNPLANNED_VISIT
import io.reactivex.Observable
import java.math.BigDecimal

class CustomerRepository(application: Application) {

    val customersDAO: CustomersDAO
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    val getCustomersLocal: LiveData<List<Customer>>
    val getCustomersList: List<Customer>
    private var clientService = ClientService.getClient(application)
    private var visitsRepository: VisitsRepository? = null
    private var appSettingsDAO: ApplicationSettingsDAO
    private var routesDAO: RoutesDAO

    val customerDataMLD = MutableLiveData<Customer>()


    init {
        customersDAO = appDB.getCustomers()
        routesDAO = appDB.getRoutes()
        appSettingsDAO = appDB.getApplicationSettings()
        getCustomersLocal = customersDAO.getAllCustomersLiveData()
        getCustomersList = customersDAO.getAllCustomers()
    }

    fun getCustomersRemote(baseBody: BaseBody): Observable<BaseResponse<Customer>> {
        return clientService.getCustomers(baseBody)
    }
    fun getCustomerData(customer:String): Customer {
        return customersDAO.getCurrentCustomer(customer)
    }

    fun upsert(customer: List<Customer>) {
        customersDAO.upsert(customer)
    }
    fun updateCustomer(customer: List<Customer>) {
        var customerListData = ArrayList<Customer>()
        customer.forEach { item ->
            if (item.operation != DELETED)
                customerListData.add(item)
        }
        customersDAO.updateCustomer(customerListData)
    }
    fun updateCustomer(customer: Customer) {
        customer.credit?.let { customersDAO.updateCustomer(it, customer.salesOrg,customer.distChannel,customer.customer) }
    }

    fun getCustomerByID(customerNo: String, salesOrg : String, distChannel : String): Customer? {
        return customersDAO.getCustomerByID(customerNo, salesOrg,distChannel)
    }

    fun createAndStartAVisit(application: Application, route: String, customer: Customer,
    latitude : Double ? = null , longitude: Double ? = null) {
        visitsRepository = VisitsRepository(application)
        val visitItemNo = visitsRepository!!.getMaxValueOfVisitItemNo()?.plus(1)
        val routeDetails = routesDAO.getRouteDetailsById(route)
        val sequence = visitsRepository!!.getSecondMaxSequenceValue()?.plus(1)
        val visitObj = Visits(
            sequence,
            customer.customizeField,
            route,
            routeDetails.division,
            AppUtils.getStringFormattedCurrentDateTime(),
            visitsRepository!!.getVisitListId(),
            visitsRepository!!.getVisitPlan(),
            customer.distChannel,
            customer.customer,
            visitItemNo!!,
            getCurrentDriver()?.value?.customer,
            customer.salesOrg,
            UNPLANNED_VISIT,
            IN_PROGRESS_VISIT,
            customer.name1,
            customer.nameArabic,
            customer.region,
            customer.regionArabic,
            customer.city,
            "",
            "",
            getFormatForEndDay(),getFormatForEndDay(),null,null,latitude,longitude,null,null
        )
        visitsRepository!!.upsert(visitObj)
    }

    fun getAllCustomer(application: Application): List<Customer> {
        visitsRepository = VisitsRepository(application)
        val customer = customersDAO.getAllCustomers()
        val filtered = arrayListOf<Customer>()
        for (i in customer.indices) {
            if (!visitsRepository!!.isACustomerAssociatedWithAVisit(customer[i].customer) && customer[i].customer != AppUtils.getDriver(
                    application
                )
            ) {
                filtered.add(customer[i])
            }
        }

        return filtered
    }

    fun getCurrentDriver(): MutableLiveData<Customer>? {
        return if (appSettingsDAO.getDriverNumber().isEmpty()){
            null
        } else {
            val driverNumber = customersDAO.getDriverData(appSettingsDAO.getDriverNumber())
            if (driverNumber.isNotEmpty())
            customerDataMLD.value = driverNumber[0]
            customerDataMLD
        }
    }

    fun getDriverMutableLiveData(id: String): LiveData<Customer> {
        return customersDAO.getDriverLiveData(id)
    }

    fun getDriverCreditLimit(id: String): BigDecimal {
        return customersDAO.getDriverCreditLimit(id).toBigDecimal()
    }

    fun deleteAllData() {
        return customersDAO.deleteDataInTable()
    }

}