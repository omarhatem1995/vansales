package com.company.vansales.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.company.vansales.app.datamodel.models.localmodels.TransactionsHistory
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.repository.TransactionsHistoryRepository
import com.company.vansales.app.datamodel.repository.TruckManagementRepository
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.utils.Constants.REQUEST_ADD

class TransactionsHistoryViewModel(application: Application) :
    AndroidViewModel(application) {

     val mRepository: TransactionsHistoryRepository =
        TransactionsHistoryRepository(application)
    val truckManagmentRepository : TruckManagementRepository = TruckManagementRepository(application)
    var allTransactionsHistoryLiveData: LiveData<List<TransactionsHistory>>
    var allTransactionsHistory: List<TransactionsHistory>
    var sharedPref = GetSharedPreferences(application)

    init {
        allTransactionsHistoryLiveData = mRepository.allTransactionsHistoryLiveData
        allTransactionsHistory = mRepository.allTransactionsHistory
    }


    fun mapMaterialsList(allTransactionsHistory: List<TransactionsHistory>): List<String> {
        val materialsList = arrayListOf<String>()
        for (i in allTransactionsHistory.indices) {
            materialsList.add(allTransactionsHistory[i].materialNumber)
        }
        return materialsList.distinct()
    }

    fun getMaterialsTransactionRecords(materialNo: String): ArrayList<TransactionsHistory> {
        val materialsList = arrayListOf<TransactionsHistory>()
        for (i in allTransactionsHistory.indices) {
            if (allTransactionsHistory[i].materialNumber == materialNo && allTransactionsHistory[i].actionType != REQUEST_ADD) {
                materialsList.add(allTransactionsHistory[i])
            }
        }
        return materialsList


    }

    fun getMaterialByMaterialNo(materialNo: String): Materials {
        return mRepository.getMaterialByMaterialNo(materialNo)
    }

    fun getMaterialByBarcode(barcode: String): Materials {
        return mRepository.getMaterialByBarcode(barcode)
    }

    fun getMaterialsOpenDayQuantity(materialNo : String)  : TransactionsHistory{
        return mRepository.getMaterialsOpenDayQuantity(materialNo)
    }



}