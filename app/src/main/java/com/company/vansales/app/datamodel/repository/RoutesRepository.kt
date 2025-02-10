package com.company.vansales.app.datamodel.repository

import android.app.Application
import com.company.vansales.app.datamodel.models.localmodels.InvoiceNumber
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.InvoiceNumberDAO
import com.company.vansales.app.datamodel.room.RoutesDAO
import kotlinx.coroutines.flow.Flow

class RoutesRepository(application: Application) {

    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    val routesDAO: RoutesDAO
    val getRoutesLocale: Flow<List<Routes>>
    private val invoiceNumberDAO: InvoiceNumberDAO

    init {
        routesDAO = appDB.getRoutes()
        getRoutesLocale = routesDAO.getRoutes()
        invoiceNumberDAO = appDB.getInvoiceNumber()
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
        for (i in routesList.indices)
        {
          routesNames.add(routesList[i].route)
        }
        return routesNames
    }

}