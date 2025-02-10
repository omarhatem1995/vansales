package com.company.vansales.app.datamodel.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.models.mastermodels.VisitsBody
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.TaxesDAO
import com.company.vansales.app.datamodel.room.VisitsDAO
import com.company.vansales.app.datamodel.services.ClientService
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.getFormatForEndDay
import com.company.vansales.app.utils.Constants.FAILED_VISIT
import com.company.vansales.app.utils.Constants.FINISHED_VISIT
import com.company.vansales.app.utils.Constants.HEADER_VISIT
import com.company.vansales.app.utils.Constants.IN_PROGRESS_VISIT
import com.company.vansales.app.utils.Constants.PLANNED_VISIT
import com.company.vansales.app.utils.Constants.UNFINISHED_VISIT
import io.reactivex.Observable
import java.util.concurrent.Executors

class VisitsRepository(application: Application) {
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    val visitsDAO: VisitsDAO = appDB.getVisits()
    val taxesDAO: TaxesDAO = appDB.getTaxes()
    private var clientService = ClientService.getClient(application)
    private val customerRepository: CustomerRepository = CustomerRepository(application)
    val getAllVisits: List<Visits> = visitsDAO.getAllVisits()
    val getAllTaxes: List<Taxes> = taxesDAO.getAllTaxes()

    fun getAllVisitsByRouteAscending(route: String): LiveData<List<Visits>> {
        return visitsDAO.getAllVisitsByRouteAscending(route)
    }/*
    fun addVisitHeader(visitHeader : List<VisitHeader>) {
        visitsDAO.addVisitHeader(visitHeader)
    }*/
    fun updateStartVisitData(visitListId : String,visitItemNo : Int,
                               startDate:String,startTime:String,startLatitude:Double , startLongitude:Double) {
        visitsDAO.updateStartVisitData(visitListId,visitItemNo,startDate,startTime,startLatitude,startLongitude)
    }

    fun updateEndVisitData(visitListId : String,visitItemNo : Int,
                               endDate:String,endTime:String,endLatitude:Double , endLongitude:Double,failedReason : String?) {
        visitsDAO.updateEndVisitData(visitListId,visitItemNo,endDate,endTime,endLatitude,endLongitude,failedReason)
    }

    fun getAllVisitsByRouteDescending(route: String): LiveData<List<Visits>> {
        return visitsDAO.getAllVisitsByRouteDescending(route)
    }

    fun getVisitByCustomerId(customerId: String): Visits {
        return visitsDAO.getVisitByCustomerId(customerId)
    }

    fun upsert(visit: Visits) {
        visitsDAO.upsert(visit)
    }

    fun upsert(visit: List<Visits>) {
        val myExecutor = Executors.newSingleThreadExecutor()
        myExecutor.execute {
            for (i in visit.indices) {
                val customer = getCustomerById(
                    visit[i].customerNo!!,
                    visit[i].salesOrg!!,
                    visit[i].dist_channel!!
                )
                if (customer != null) {
                    val visitObj = Visits(
                        visit[i].sequence,
                        visit[i].customizeField,
                        visit[i].route,
                        visit[i].division,
                        visit[i].excutionDate,
                        visit[i].visitListID,
                        visit[i].visitPlan,
                        visit[i].dist_channel,
                        visit[i].customerNo,
                        visit[i].visitItemNo,
                        visit[i].driver,
                        visit[i].salesOrg,
                        PLANNED_VISIT,
                        UNFINISHED_VISIT,
                        customer.name1,
                        AppUtils.decodeArabicString(customer.nameArabic!!),
                        customer.region,
                        customer.regionArabic,
                        customer.city,
                        "",
                        "",
                        null,null,null,null,null,null,null,null
                    )
                    visitsDAO.upsert(visitObj)

                }
            }
        }
    }
    fun deleteAllData() {
        return visitsDAO.deleteDataInTable()
    }
    fun update(visit: List<Visits>) {
        val myExecutor = Executors.newSingleThreadExecutor()
        var route = ""
        var division = ""
        var excutionDate = ""
        var visitListID = ""
        var visitPlan = ""
        var dist_channel = ""
        var driver = ""
        var visitItemNo = 0
        var salesOrg = ""

        myExecutor.execute {

            for (i in visit.indices) {
                val customer = getCustomerById(
                    visit[i].customerNo!!,
                    visit[i].salesOrg!!,
                    visit[i].dist_channel!!
                )
                if (customer != null) {
                    val visitObj = Visits(
                        visit[i].sequence,
                        visit[i].customizeField,
                        visit[i].route,
                        visit[i].division,
                        visit[i].excutionDate,
                        visit[i].visitListID,
                        visit[i].visitPlan,
                        visit[i].dist_channel,
                        visit[i].customerNo,
                        visit[i].visitItemNo,
                        visit[i].driver,
                        visit[i].salesOrg,
                        PLANNED_VISIT,
                        UNFINISHED_VISIT,
                        customer.name1,
                        AppUtils.decodeArabicString(customer.nameArabic!!),
                        customer.region,
                        customer.regionArabic,
                        customer.city,
                        "",
                        "",
                        null,null,null,null,null,null,null,null
                    )
                     route = visitObj.route.toString()
                     division = visitObj.division.toString()
                     excutionDate = visitObj.excutionDate.toString()
                     visitListID = visitObj.visitListID
                     visitPlan = visitObj.visitPlan.toString()
                     dist_channel = visitObj.dist_channel.toString()
                     driver = visitObj.driver.toString()
                     visitItemNo = visitObj.visitItemNo
                     salesOrg = visitObj.salesOrg.toString()
                    visitsDAO.updateVisits(visitObj)
                }
            }
            var driverObject = Visits(123456789,"",route,division,
                excutionDate,visitListID,visitPlan,
                dist_channel,driver,visitItemNo +1,driver,
                salesOrg,HEADER_VISIT,HEADER_VISIT,
                "","","","","","","",
                getFormatForEndDay(),getFormatForEndDay(),null,null,null,null,null,null)
            visitsDAO.updateVisits(driverObject)
        }
    }

    fun getVisitsRemote(visitsBody: VisitsBody): Observable<BaseResponse<Visits>> {
        return clientService.getVisits(visitsBody)
    }

    fun getCustomerById(customerNo: String, salesOrg: String, distChannel: String): Customer? {
        return customerRepository.getCustomerByID(customerNo, salesOrg, distChannel)
    }

    fun startVisit(visitListID: String, visitItemNo: Int) {
        visitsDAO.startVisit(visitListID, visitItemNo, IN_PROGRESS_VISIT)
    }

    fun finishVisit(visitListID: String, visitItemNo: Int) {
        visitsDAO.finishVisit(visitListID, visitItemNo, FINISHED_VISIT)
    }

    fun finishFailedVisit(
        visitListID: String,
        visitItemNo: Int,
        failingReason: String,
        failingReasonID: String
    ) {
        visitsDAO.finishFailedVisit(
            visitListID,
            visitItemNo,
            FAILED_VISIT,
            failingReason,
            failingReasonID
        )
    }

    fun isAVisitCurrentlyInProgress(): Boolean {
        return visitsDAO.isVisitCurrentlyInProgress(IN_PROGRESS_VISIT)
    }
    fun finishCurrentVisit() {
        return visitsDAO.finishCurrentVisit(FINISHED_VISIT,IN_PROGRESS_VISIT)
    }

    fun getCurrentActiveVisit(): Visits? {
        return visitsDAO.getCurrentActiveVisit(IN_PROGRESS_VISIT)
    }

    fun getCurrentActiveVisitLiveData(): LiveData<Visits> {
        return visitsDAO.getCurrentActiveVisitLiveData(IN_PROGRESS_VISIT)
    }

    fun getFinishedVisits(): List<Visits> {
        return visitsDAO.getFinishedVisits(FINISHED_VISIT, FAILED_VISIT)
    }

    fun getAllFinishedVisits(): List<Visits> {
        return visitsDAO.getAllFinishedVisits()
    }

    fun getMaxValueOfVisitItemNo(): Int? {
        return visitsDAO.getMaxvisitItemNoValue()
    }

    fun getMaxValueOfSequence(): Int? {
        return visitsDAO.getMaxSequenceValue()
    }
    fun getSecondMaxSequenceValue(): Int? {
        return visitsDAO.getSecondMaxSequenceValue()
    }

    fun getVisitListId(): String {
        return visitsDAO.getVisitListId()
    }

    fun getVisitPlan(): String {
        return visitsDAO.getVisitPlan()
    }

    fun isACustomerAssociatedWithAVisit(customerNo: String): Boolean {
        return visitsDAO.isACustomerAssociatedWithAVisit(customerNo)
    }

    fun regionsList(): ArrayList<String> {
        val regions = ArrayList<String>()
        for (i in getAllVisits.indices) {
            regions.add(
                getCustomerById(
                    getAllVisits[i].customerNo!!,
                    getAllVisits[i].salesOrg!!,
                    getAllVisits[i].dist_channel!!
                )?.region!!
            )
        }
        return regions
    }

    fun citiesList(): ArrayList<String> {
        val cities = ArrayList<String>()
        for (i in getAllVisits.indices) {
            cities.add(
                getCustomerById(
                    getAllVisits[i].customerNo!!,
                    getAllVisits[i].salesOrg!!,
                    getAllVisits[i].dist_channel!!
                )?.city!!
            )
        }
        return cities
    }

    fun routesList(): List<String> {
        val visits = getAllVisits
        val routes = arrayListOf<String>()
        for (i in visits.indices) {
            routes.add(visits[i].route!!)
        }
        return routes.distinct()
    }

    private fun updateCurrentActiveVisit() {
        visitsDAO.updateCurrentActiveVisit(IN_PROGRESS_VISIT, FAILED_VISIT)
    }

    fun onRouteChange(
        application: Application,
        route: String,
        customerNo: String,
        salesOrg: String,
        distChannel: String
    ) {
        if (visitsDAO.isACustomerWithSpecificRouteAssociatedWithAvisit(route, customerNo)) {
            updateCurrentActiveVisit()
            val visit = visitsDAO.getVisitByRouteAndCustomerNumber(route, customerNo)
            startVisit(visit.visitListID, visit.visitItemNo)
        } else {
            updateCurrentActiveVisit()
            getCustomerById(customerNo, salesOrg, distChannel)?.let {
                customerRepository.createAndStartAVisit(
                    application,
                    route,
                    it
                )
            }
        }
    }

}