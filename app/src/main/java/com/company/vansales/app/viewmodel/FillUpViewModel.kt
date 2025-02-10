package com.company.vansales.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.company.vansales.app.datamodel.models.localmodels.BatchItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocHeader
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.Deliveries
import com.company.vansales.app.datamodel.models.mastermodels.DeliveriesBody
import com.company.vansales.app.datamodel.models.mastermodels.Delivery
import com.company.vansales.app.datamodel.repository.FillUpRepository
import com.company.vansales.app.datamodel.repository.SalesDocRepository
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.utils.Constants.NUMBER_OF_RETRIES
import com.company.vansales.app.utils.Constants.STATUS_EMPTY
import com.company.vansales.app.utils.Constants.STATUS_ERROR
import com.company.vansales.app.utils.Constants.STATUS_LOADED
import com.company.vansales.app.utils.DocumentsConstants.DELIVERY
import com.company.vansales.app.utils.DocumentsConstants.LOAD_ORDER
import com.company.vansales.app.utils.DocumentsConstants.PROMO_DELIVERY
import com.company.vansales.app.utils.DocumentsConstants.TOP_UP_ORDER
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class FillUpViewModel(application: Application) :
    AndroidViewModel(application) {

    var sharedPref = GetSharedPreferences(application)

    private val mRepository: FillUpRepository = FillUpRepository(application)
    private val salesDocRepository: SalesDocRepository = SalesDocRepository(application)
    var compositeDisposable = CompositeDisposable()

    val closeKeyboard = MutableLiveData<Boolean>()

    //Deliveries  Error Handling
    private val deliveriesStatusMutableLiveData = MutableLiveData<String>()
    val deliveriesStatusLiveData: LiveData<String>
        get() = deliveriesStatusMutableLiveData

    //Delivery Check  Error Handling
    private val deliveryCheckStatusMutableLiveData = MutableLiveData<String>()
    val deliveriyCheckStatusLiveData: LiveData<String>
        get() = deliveryCheckStatusMutableLiveData


    private val deliveriesMutableLiveData = MutableLiveData<Deliveries>()
    val deliveriesLiveData: LiveData<Deliveries>
        get() = deliveriesMutableLiveData

    var fillUpList: List<Delivery>? = null
    var loadUpTopUpList: ArrayList<Delivery>? = null
    var deliveriesList: ArrayList<Delivery>? = null
    var totalQuantityFillUp = 0.0

    fun getSalesDocHeaderWithERPInvoiceNumber(erpInvoice : String) :LiveData<SalesDocHeader>{
        return salesDocRepository.getSalesDocHeaderWithERPInvoiceNumber(erpInvoice)
    }

    fun mapSalesDocToISalesDoc(salesDocHeader : SalesDocHeader) : ISalesDocHeader?{
            var  iSalesDocHeader = salesDocHeader.salesOrg?.let { salesOrg ->
                salesDocHeader.distChannel?.let { distChannel ->
                    ISalesDocHeader(
                        salesorg = salesOrg,
                        distchannel = distChannel,
                        division = "",
                        plant = salesDocHeader.plant,
                        location = salesDocHeader.location,
                        route = " ",
                        driver = salesDocHeader.customer,
                        customer = salesDocHeader.customer,
                        doctype = salesDocHeader.docType!!,
                        exdoc = salesDocHeader.exInvoice,
                        creationdate = Date(),
                        creationtiem = Date(),
                        totalvalue = salesDocHeader.totalValue!!.toBigDecimal(),
                        discountValue = salesDocHeader.totalDiscount!!.toBigDecimal(),
                        paymentterm = salesDocHeader.paymentTerm.toString(),
                        docstatus = salesDocHeader.docStatus!!,
                        customizeheader = "",
                        visitid = salesDocHeader.visitId.toString(),
                        sapDoc = salesDocHeader.erpInvoice,
                        visititem = salesDocHeader.visitItem.toString()
                    )
                }
        }
        return iSalesDocHeader
    }

    fun getDeliveriesRemote(baseBody: BaseBody, x : String){

    }

    fun getDeliveriesRemote(baseBody: BaseBody) {
        val deliveriesDisposable =
            mRepository.getDeliveriesListRemote(baseBody)
                .subscribeOn(Schedulers.io())
                .retry(NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { deliveries ->
                        fillUpList = deliveries.data
                        getLoadUpAndTopUpList(deliveries.data)
//                        getDeliveriesList(deliveries.data)
                        if (deliveries.data.isEmpty()) {
                            deliveriesStatusMutableLiveData.value = STATUS_EMPTY
                        } else {
                            deliveriesStatusMutableLiveData.value = STATUS_LOADED

                        }
                    },
                    {
                        deliveriesStatusMutableLiveData.value = STATUS_ERROR
                    })
        compositeDisposable.add(deliveriesDisposable)
    }

    fun getDeliveryCheckRemote(deliveriesBody: DeliveriesBody) {
        val deliveryCheckDisposable =
            mRepository.getDeliveriesCheckRemote(deliveriesBody)
                .subscribeOn(Schedulers.io())
                .retry(NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { deliveries ->
                        deliveriesMutableLiveData.value = deliveries.data
                        deliveryCheckStatusMutableLiveData.value = STATUS_LOADED
                    },
                    {
                        deliveryCheckStatusMutableLiveData.value = STATUS_ERROR
                    })
        compositeDisposable.add(deliveryCheckDisposable)
    }


    private fun getLoadUpAndTopUpList(items: List<Delivery>): List<Delivery>? {
        loadUpTopUpList = arrayListOf()
        for (i in items.indices) {
            if (items[i].deliveryType == TOP_UP_ORDER || items[i].deliveryType == LOAD_ORDER) {
                loadUpTopUpList!!.add(items[i])
            }
        }
        return loadUpTopUpList
    }

    private fun getDeliveriesList(items: List<Delivery>): List<Delivery>? {
        deliveriesList = arrayListOf()
        for (i in items.indices) {
            if (items[i].deliveryType == DELIVERY || items[i].deliveryType == PROMO_DELIVERY) {
                deliveriesList!!.add(items[i])
            }
        }
        return deliveriesList
    }

    fun typesList(): ArrayList<String>? {
        val types = ArrayList<String>()
        for (i in fillUpList!!.indices) {
            when (fillUpList!![i].deliveryType) {
                TOP_UP_ORDER -> types.add("Top Up")
                LOAD_ORDER -> types.add("Load Up")
                DELIVERY -> types.add("Delivery")
                PROMO_DELIVERY -> types.add("Promo Delivery")
            }
        }

        return types
    }


    fun plantsList(): ArrayList<String>? {
        val plant = ArrayList<String>()
/*
        for (i in fillUpList!!.indices) {
            if (fillUpList!![i].plant != null) {
                plant.add(fillUpList!![i].plant!!)
            }
        }
*/
        return plant

    }

    fun salesOrgsList(): ArrayList<String>? {
        val salesOrg = ArrayList<String>()
        for (i in fillUpList!!.indices) {
            salesOrg.add(fillUpList!![i].salesOrg!!)
        }

        return salesOrg
    }


    fun createdByList(): ArrayList<String>? {
        val createdBy = ArrayList<String>()
        for (i in fillUpList!!.indices) {
            createdBy.add(fillUpList!![i].createBy!!)
        }
        return createdBy
    }

    fun getSelectedTypValue(selectedType: String?): String {
        return when (selectedType) {
            "Top Up" -> TOP_UP_ORDER
            "Load Up" -> LOAD_ORDER
            "Delivery" -> DELIVERY
            else -> PROMO_DELIVERY
        }
    }

    val itemBatches: MutableList<BatchItems> = ArrayList()
    fun checkAllAccepted():Boolean{
        for(i in itemBatches) {
            if(i.requiredQuantity == i.selectedQuantity){
                continue
            }
            else
                return false
        }
        return true
    }
    fun checkItemCountWithBatch(batchNumber : String) : Double{
        var materialNumber  = ""
        var selectedQuantity = 0.0
        for( i in itemBatches){
            if(i.batchNo == batchNumber) {
                materialNumber = i.materialNumber
                break
            }else{
                continue
            }
        }
        for(i in itemBatches){
            if(materialNumber == i.materialNumber && batchNumber != i.batchNo){
                selectedQuantity +=  i.selectedQuantity
            }
        }
        return selectedQuantity
    }

    fun checkMaterialFullyLoaded(materialNumber:String):Boolean {
        for (i in itemBatches) {
            if (materialNumber.equals(i.materialNumber)) {
                if (i.requiredQuantity == i.selectedQuantity) {
                    continue
                } else return false
            }
        }
        return true
    }
}