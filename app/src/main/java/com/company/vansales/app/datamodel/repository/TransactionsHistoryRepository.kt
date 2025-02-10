package com.company.vansales.app.datamodel.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.company.vansales.app.datamodel.models.localmodels.TransactionsHistory
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.MaterialsDAO
import com.company.vansales.app.datamodel.room.TransactionsHistoryDAO
import com.company.vansales.app.utils.Constants.OPEN_DAY_STOCK

class TransactionsHistoryRepository(application: Application) {
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    val transactionsHistoryDAO: TransactionsHistoryDAO
    val materialsDAO: MaterialsDAO
    val allTransactionsHistoryLiveData: LiveData<List<TransactionsHistory>>
    val allTransactionsHistory: List<TransactionsHistory>

    init {
        materialsDAO = appDB.getMaterials()
        transactionsHistoryDAO = appDB.getTransactionsHistory()
        allTransactionsHistoryLiveData =
            transactionsHistoryDAO.getAllTransactionsHistoryLiveData(OPEN_DAY_STOCK)
        allTransactionsHistory =
            transactionsHistoryDAO.getAllTransactionsHistory(OPEN_DAY_STOCK)
    }

    fun getMaterialByMaterialNo(materialNo: String): Materials {
        return materialsDAO.getMaterialByMaterialNo(materialNo)
    }

    fun getMaterialByBarcode(barcode: String): Materials {
        return materialsDAO.getMaterialByBarcode(barcode)
    }

    fun getMaterialsOpenDayQuantity(materialNo: String): TransactionsHistory {
        return transactionsHistoryDAO.getMaterialsOpenDayQuantity(
            OPEN_DAY_STOCK,
            materialNo
        )
    }


}
