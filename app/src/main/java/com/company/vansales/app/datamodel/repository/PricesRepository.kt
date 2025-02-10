package com.company.vansales.app.datamodel.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.MaterialsUnit
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.PricesDAO
import com.company.vansales.app.datamodel.services.ClientService
import io.reactivex.Observable

class PricesRepository(application: Application) {

    private var clientService = ClientService.createServiceFiveMinutesTimeOut(application)
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    val pricesDAO: PricesDAO
    private lateinit var materialsUnitRepository: MaterialsUnitRepository
    val getPricesLocale: LiveData<List<Prices>>

    init {
        pricesDAO = appDB.getPrices()
        getPricesLocale = pricesDAO.getPrices()
    }

    fun upsert(PricesList: List<Prices>) {
        pricesDAO.upsert(PricesList)
    }
    fun update(PricesList: List<Prices>) {
        pricesDAO.updatePrices(PricesList)
    }
    fun deleteItems(PricesList: List<Prices>) {
        pricesDAO.deletePrices(PricesList)
    }

    fun getPricesRemotely(baseBody: BaseBody): Observable<BaseResponse<Prices>> {
        return clientService.getPrices(baseBody)
    }

    fun getPricesByPrimary(
        materialNumber: String,
        customerId: String,
        distChannel: String
    ): List<Prices> {
        return pricesDAO.getPricesByPrimary(materialNumber, customerId, distChannel)
    }
    fun getMaterialPriceByPrimary(
        materialNumber: String,
        customerId: String,
        distChannel: String
    ): Prices {
        return pricesDAO.getMaterialPriceByPrimary(materialNumber, customerId, distChannel)
    }

    fun getMaterialsUnitByPrimary(
        application: Application,
        materialNumber: String,
        unit: String
    ): List<MaterialsUnit> {
        materialsUnitRepository = MaterialsUnitRepository(application)
        return materialsUnitRepository.getMaterialsUnitByPrimary(materialNumber, unit)
    }
}
