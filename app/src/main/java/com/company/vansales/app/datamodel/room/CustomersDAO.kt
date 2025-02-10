package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import java.math.BigDecimal

@Dao
abstract class CustomersDAO : BaseDao<Customer>() {

    @Query("SELECT  * FROM customers ORDER BY name1 ASC")
    abstract fun getAllCustomersLiveData(): LiveData<List<Customer>>

    @Query("SELECT  * FROM customers WHERE customer != :driver ORDER BY name1 ASC ")
    abstract fun getAllCustomers(driver:String): List<Customer>

    @Query("SELECT  * FROM customers ORDER BY name1 ASC ")
    abstract fun getAllCustomers(): List<Customer>

    @Query("SELECT * FROM customers WHERE customer  = :id AND salesOrg = :salesOrg AND distChannel = :distChannel")
    abstract fun getCustomerByID(id: String, salesOrg : String, distChannel : String ): Customer?

    @Query("SELECT * FROM customers WHERE customer  COLLATE NOCASE= :id")
    abstract fun getDriverData(id: String) : List<Customer>

    @Query("DELETE FROM customers")
    abstract fun deleteAllCustomers()

    @Query("SELECT * FROM customers WHERE customer  = :id")
    abstract fun getDriverLiveData(id: String) : LiveData<Customer>

    @Query("SELECT credit FROM customers WHERE customer  = :id")
    abstract fun getDriverCreditLimit(id: String) : Double

    @Query("SELECT * FROM customers WHERE customer  = :id")
    abstract fun getCurrentCustomer(id: String) : Customer

    @Query("DELETE FROM customers")
    abstract fun deleteDataInTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateCustomer(customer: List<Customer>)

    @Query("UPDATE customers SET credit = :credit " +
            "WHERE salesOrg = :salesOrg AND distChannel = :distChannel AND customer = :customer")
    abstract fun updateCustomer(credit :Double,
                                salesOrg : String, distChannel : String
                                ,customer : String)
}