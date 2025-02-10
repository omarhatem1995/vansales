package com.company.vansales.app.datamodel.repository

import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.localmodels.InvoiceNumber
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.InvoiceNumberDAO
import com.company.vansales.app.datamodel.room.RoutesDAO
import com.company.vansales.app.datamodel.services.api.GetRoutesGateway
import com.company.vansales.app.domain.repo.RoutesRepo
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRoutesRepositoryImpl @Inject constructor(
    private val getRoutesGateway: GetRoutesGateway,
    private val appDB: AppDataBase
) : RoutesRepo {

    private val routesDAO: RoutesDAO = appDB.getRoutes()
    val getRoutesLocale: Flow<List<Routes>> = routesDAO.getRoutes()
    private val invoiceNumberDAO: InvoiceNumberDAO = appDB.getInvoiceNumber()

    override suspend fun getRoutes(baseBody: BaseBody): ApiResponse<BaseResponse<Routes>, Error> {
        return getRoutesGateway.getRoutes(baseBody)
    }

    fun upsert(routes: List<Routes>) {
        routesDAO.deleteAllRoutes()
        routesDAO.upsert(routes)
        initializeInvoiceNumbersPerRoute(routes)
    }

    fun upsert(invoiceNumber: InvoiceNumber) {
        invoiceNumberDAO.upsert(invoiceNumber)
    }

    private fun initializeInvoiceNumbersPerRoute(routes: List<Routes>) {
        for (i in routes.indices) {
            val invoiceNumber = InvoiceNumber("0000000", routes[i].route)
            invoiceNumberDAO.upsert(invoiceNumber)
        }
    }

    fun getRoutesListString(): ArrayList<String> {
        val routesList = routesDAO.getRoutesList()
        val routesNames = ArrayList<String>()
        for (i in routesList.indices) {
            routesNames.add(routesList[i].route)
        }
        return routesNames
    }
}