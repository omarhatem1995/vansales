package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.localmodels.TransactionsHistory

@Dao
abstract class TransactionsHistoryDAO : BaseDao<TransactionsHistory>() {
    @Query("SELECT * FROM transactions_history WHERE actionType != :type")
    abstract fun getAllTransactionsHistoryLiveData(type: String): LiveData<List<TransactionsHistory>>

    @Query("SELECT * FROM transactions_history WHERE actionType != :type")
    abstract fun getAllTransactionsHistory(type: String): List<TransactionsHistory>


    @Query("SELECT * FROM transactions_history WHERE actionType = :type AND materialNumber = :materialNo")
    abstract fun getMaterialsOpenDayQuantity(type: String, materialNo: String): TransactionsHistory

    @Query("SELECT * FROM transactions_history WHERE materialNumber = :materialNo")
    abstract fun getAllTransactionHistory(materialNo: String): List<TransactionsHistory>


    @Query("SELECT MAX(id) FROM transactions_history")
    abstract fun getMaxIdNumber(): Int


    @Query("DELETE FROM transactions_history")
    abstract fun deleteAllTransactions()

}