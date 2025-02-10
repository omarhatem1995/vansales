package com.company.vansales.app.datamodel.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.ItemPriceConditionDAO
import com.company.vansales.app.datamodel.services.ClientService
import io.reactivex.Observable

class ItemPriceConditionRepository(application: Application) {

    private var clientService = ClientService.createServiceFiveMinutesTimeOut(application)
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    val itemPriceConditionDAO: ItemPriceConditionDAO = appDB.getItemPriceConditions()
    val getItemPriceConditionLocale: LiveData<List<ItemPriceCondition>> = itemPriceConditionDAO.getItemPriceConditions(customer = "SCJ001201")

    fun upsert(itemPriceCondition: List<ItemPriceCondition>) {
        itemPriceConditionDAO.upsert(itemPriceCondition)
    }
    fun update(itemPriceCondition: List<ItemPriceCondition>) {
        itemPriceConditionDAO.updatePriceConditions(itemPriceCondition)
    }
    fun deleteItems(itemsPriceCondition: List<ItemPriceCondition>) {
        itemPriceConditionDAO.deletePriceConditions(itemsPriceCondition)
    }

    fun deleteItem(itemPriceCondition: ItemPriceCondition) {
        itemPriceConditionDAO.deleteItemCondition(itemPriceCondition.customer,
            itemPriceCondition.distChannel,itemPriceCondition.salesOrg,itemPriceCondition.materialNo,
        itemPriceCondition.condition,itemPriceCondition.condCounter)
    }

    fun getItemPriceConditionRemotely(baseBody: BaseBody): Observable<BaseResponse<ItemPriceCondition>> {
        return clientService.getItemPriceCondition(baseBody)
    }

    fun getItemPriceConditionsByPrimary(
        materialNumber: String,
        customerId: String,
        distChannel: String,
        salesOrg : String
    ): List<ItemPriceCondition> {
        return itemPriceConditionDAO.getItemPriceConditionsByPrimary(
            materialNumber,
            customerId,
            distChannel,
            salesOrg
        )
    }

}
