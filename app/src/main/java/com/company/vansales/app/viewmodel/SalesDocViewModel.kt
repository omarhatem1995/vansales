package com.company.vansales.app.viewmodel

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocItems
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.repository.SalesDocRepository
import com.company.vansales.app.datamodel.services.ClientService
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.utils.Constants.NUMBER_OF_RETRIES
import com.company.vansales.app.utils.Constants.STATUS_ERROR
import com.company.vansales.app.utils.Constants.STATUS_LOADED
import com.company.vansales.app.utils.enums.DocumentsEnum
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.io.File

class SalesDocViewModel(application: Application) :
    AndroidViewModel(application) {

    private val mRepository: SalesDocRepository = SalesDocRepository(application)
    var clientService = ClientService.getClient(application)
    var docNumber: ((String, String) -> Unit)? = null
    var createDocumentStatusMLD = MutableLiveData<String>()
    val createDocumentStatusLD: LiveData<String>
        get() = createDocumentStatusMLD
    var compositeDisposable = CompositeDisposable()
    var sharedPref = GetSharedPreferences(application)


    fun saveDocument(
        exInvoiceNumber: String,
        mCurrentLocation: Location?,
        iSalesDocHeader: ISalesDocHeader,
        headerItems: List<HeaderItems>,
        headerBatches: List<HeaderBatches>,
        erpInvoiceNumber: String?,
        status: DocumentsEnum
    ) {
        Log.d("enteredCreated" , "saved ${headerItems.size}")
        mRepository.saveDocument(
            exInvoiceNumber,
            mCurrentLocation,
            iSalesDocHeader,
            headerItems,
            headerBatches,
            erpInvoiceNumber,
            status
        )
    }

    fun getAllSalesDocsHeaders(): LiveData<List<SalesDocHeader>> {
        return mRepository.getAllSalesDocHeaderWithStatusApproval()
    }

    fun getCustomerByID(customerID: String, salesOrg: String, distChannel: String): Customer? {
        return mRepository.getCustomerByCustomerNumber(customerID, salesOrg, distChannel)
    }

    fun getSalesDocHeader(exInvoiceNumber: String): SalesDocHeader {
        return mRepository.getSalesDocHeader(exInvoiceNumber)
    }

    fun getAllSalesDocItems(): LiveData<List<SalesDocItems>> {
        return mRepository.getAllSalesDocItems()
    }
    fun getAllSalesDocItemsNotFree(exInvoice: String): LiveData<List<SalesDocItems>> {
        return mRepository.getAllSalesDocItemsNotFree(exInvoice)
    }
    fun getSalesDocItemsWithInvoiceNumber(exInvoice: String): LiveData<List<SalesDocItems>> {
        return mRepository.getSalesDocItemsWithInvoiceNumber(exInvoice)
    }

    fun getAllSalesDocFree(exInvoice: String): LiveData<List<SalesDocItems>> {
        return mRepository.getAllSalesDocFree(exInvoice)
    }

    fun postOfflineDocument(exInvoiceNumber: String) {
        postResults(mRepository.postOfflineDocument(exInvoiceNumber), exInvoiceNumber)
    }

    fun deleteAllData() {
    }

    private fun postResults(salesDocBody: SalesDocBody, exInvoiceNumber: String) {
        val createDocumentDisposable =
            clientService.createDocument(salesDocBody)
                .subscribeOn(Schedulers.io())
                .retry(NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.data[0].messagetype == "S") {
                        createDocumentStatusMLD.value = STATUS_LOADED
                        docNumber!!.invoke(response.data[0].message1, exInvoiceNumber)
                    } else {
                        createDocumentStatusMLD.value = response.data[0].message2
                    }
                }, {
                    createDocumentStatusMLD.value = STATUS_ERROR
                })

        compositeDisposable.add(createDocumentDisposable)
    }

    fun updateSalesDocHeaderStatus(exInvoiceNumber: String, erpInvoice: String) {
        mRepository.updateSalesDocHeaderStatus(
            exInvoiceNumber,
            erpInvoice
        )
    }

    fun getCustomerSalesDocHeaders(customer: String): LiveData<List<SalesDocHeader>> {
        return mRepository.getCustomerSalesDocHeaders(customer)
    }

    fun getAllRequestsSalesDocHeaders(): LiveData<List<SalesDocHeader>> {
        return mRepository.getAllRequestsSalesDocHeaders()
    }

    fun getAllInvoicesDocuments(): LiveData<List<SalesDocHeader>> {
        return mRepository.getAllInvoicesDocuments()
    }
    fun getAllInvoicesDocumentsList(): List<SalesDocHeader> {
        return mRepository.getAllInvoicesDocumentsList()
    }



}