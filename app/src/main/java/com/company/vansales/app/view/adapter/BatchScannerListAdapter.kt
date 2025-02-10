package com.company.vansales.app.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.R
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.repository.ConvertUnit
import com.company.vansales.app.datamodel.repository.TruckManagementRepository
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
import com.company.vansales.app.utils.enums.BatchScanTypesEnum
import com.company.vansales.app.viewmodel.BatchScanViewModel
import com.company.vansales.databinding.ItemBatchScannerBinding

class BatchScannerListAdapter(
    private val headerItems: List<HeaderItems>,
    private val headerBatches: MutableList<HeaderBatches>,
    private var batchScanViewModel: BatchScanViewModel,
    private var batchScanEnum: BatchScanTypesEnum
) :
    RecyclerView.Adapter<BatchScannerListAdapter.ViewHolder>() {

    private lateinit var headerItem: HeaderItems
    private var currentHeaderItem: HeaderBatches? = null
    private var countAvailable: Double = 0.0
    private var batchAdapter: BatchListAdapter? = null
    private lateinit var context: Context
    private lateinit var lastItemHolder: ViewHolder
    private var lastItemPosition: Int = -1
    var updateItems: ((String) -> Unit)? = null
    var convertUnit = ConvertUnit(application)
    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: ItemBatchScannerBinding =
            DataBindingUtil.inflate(
                this.layoutInflater!!,
                R.layout.item_batch_scanner, parent, false
            )
        return ViewHolder(
            binding
        )
    }

    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        headerItem = headerItems[position]
        val params = holder.itemView.layoutParams
        if(!batchScanViewModel.isReturn && batchScanEnum != BatchScanTypesEnum.FILL_UP) {
            headerBatches.clear()
            headerBatches.addAll(setupHeaderBatches(headerItem))
        }
        initBatchesList(holder, headerItem,headerItem.isFocused)
        holder.binding.itemDescriptionTV.text = AppUtils.getLanguageDependencyString(
            headerItem.materialDescription,
            headerItem.materialDescriptionArabic,
            application
        )
        if (headerBatches.isNotEmpty()) {
            countAvailable = AppUtils.round(headerBatches.first().requestedQuantity!!)
        }
        Log.d("headerItem", headerItem.countedQuantity.toString())
        holder.binding.materialNumberTV.text = headerItem.materialno
        holder.binding.itemUnitsTV.text = headerItem.uom
        holder.binding.itemNumberTV.text = headerItem.itemno
        holder.binding.itemCountTV.text = this.context.resources.getString(
            R.string.count,
            headerItem.countedQuantity?.toString(),
            AppUtils.round(headerItem.requestedQuantity!!).toString()
        )

        if (headerItem.isFocused) {
            expandCollapseItem(holder, position)
        }
        batchAdapter?.updateUI = {
            if (!holder.binding.batchRV.isComputingLayout) {
                try{
                notifyDataSetChanged()
                updateItems?.invoke("")
                }catch (exception :Exception){
                    Log.d("exception"," exception : $exception")
                }
            }
        }
        if (headerItem.itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
            || headerItem.itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
        ) {
            holder.itemView.visibility = View.GONE
            params.height = 0
            params.width = 0
            holder.itemView.layoutParams = params
        }
        holder.itemView.setOnClickListener {
            var isContained = false
            headerBatches.forEach {
                if(it.itemno == headerItem.itemno) {
                    isContained = true
                    return@forEach
                }
            }

            if(!isContained){
                Toast.makeText(context,"No Batch for this material",Toast.LENGTH_LONG).show()
            }
                if (!batchScanViewModel.isReturn)
                    expandCollapseItem(holder, position)
            /*    } else {
                    Toast.makeText(context, "You have already chosen batches", Toast.LENGTH_LONG).show()
                }*/
        }
        batchScanViewModel.hideCountedMLD.observe(context as LifecycleOwner) { hide ->
            if (hide!!) {
                if (headerItem.countedQuantity ==
                    headerItem.requestedQuantity
                ) {
                    holder.itemView.visibility = View.GONE
                    params.height = 0
                    params.width = 0
                    holder.itemView.layoutParams = params
                }
            } else {
                if (headerItem.itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
                    || headerItem.itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
                ) {
                    holder.itemView.visibility = View.VISIBLE
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT
                    holder.itemView.layoutParams = params
                }
            }
        }

    }

    /**
     * user now should expand specific item
     * and make focus on editText
     */

    fun requestFocus(position: Int) {
        headerItems[position].isFocused = true
        notifyItemChanged(position)
    }

    private fun expandCollapseItem(
        holder: ViewHolder,
        position: Int
    ) {
   /*     if (holder.binding.expandableLayout.isExpanded) {
            holder.binding.expandableLayout.collapse()
            batchScanViewModel.closeKeyboard.value = true
            lastItemHolder = holder
            lastItemPosition = position
        } else {
            if (lastItemPosition != -1)
                collapseLastItem()
            holder.binding.expandableLayout.expand()
            lastItemHolder = holder
            lastItemPosition = position
        }*/
    }


    private fun collapseLastItem() {
    /*    if (lastItemHolder.binding.expandableLayout.isExpanded) {
            lastItemHolder.binding.expandableLayout.collapse()
            batchScanViewModel.closeKeyboard.value = true
        }*/
    }

    private fun initBatchesList(holder: ViewHolder,headerItem : HeaderItems, focused: Boolean) {
        val itemBatches: MutableList<HeaderBatches> = ArrayList()
        for (i in headerBatches.indices) {
            Log.d("getHeaderBatches", "${headerBatches[i].itemno}")
            if(headerBatches[i].materialno == headerItem.materialno) {
//                headerItem.itemno = headerBatches[i].itemno
                itemBatches.add(headerBatches[i])
            }
        }
        if (!itemBatches.isNullOrEmpty()) {
            initBatchesRecyclerView(holder, itemBatches, focused)
        }
    }

    private fun setupHeaderBatches(item: HeaderItems): MutableList<HeaderBatches> {
        val truckManagementRepository = TruckManagementRepository(application)
        val materialsBatchesList = mutableListOf<HeaderBatches>()

        if (item.itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
            || item.itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
        ) {
            if (item.itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE &&
                item.itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
            ) {

                val truckBatches =
                    truckManagementRepository.getSellableBatchesAndDeliveriesByMaterialNo(
                        item.materialno!!,
                        item.salesOrg
                    )
                for (j in truckBatches.indices) {
                    val truckBatch = truckBatches[j]
                    val headerBatch = HeaderBatches(
                        exdoc = item.exdoc,
                        itemno = item.itemno,
                        batch = truckBatch.batch,
                        materialno = item.materialno,
                        parentBaseRequestedQuantity = convertUnit.convertUnit(
                            item.materialno!!,
                            truckBatch.uom!!,
                            item.uom!!,
                            truckBatch.available!!
                        ),
                        baseRequestedQuantity = convertUnit.convertUnit(
                            item.materialno!!,
                            truckBatch.uom!!,
                            item.uom!!,
                            truckBatch.available!!
                        ),
                        baseUnit = item.uom,
                        baseCounted = 0.0,
                        requestedQuantity = convertUnit.convertUnit(
                            item.materialno!!,
                            truckBatch.uom!!,
                            item.uom!!,
                            truckBatch.available!!
                        ),
                        countedQuantity = 0.0,
                        expiryDate = truckBatch.expiryDate,
                        uom = item.uom,
                        bType = truckBatch.mtype,
                        customizebatch = "",
                        storageLocation = item.storageLocation,
                        plant = item.plant
                    )
                    materialsBatchesList.add(headerBatch)
                }
            }
        }
        return materialsBatchesList
    }
    private fun setupHeaderBatchesFillUp(item: HeaderItems , batches:MutableList<HeaderBatches>):
            MutableList<HeaderBatches> {
        val materialsBatchesList = mutableListOf<HeaderBatches>()
        Log.d("getItems" , "$item , ${batches.size}")
                for (j in batches.indices) {
                    val truckBatch = batches[j]
                    val headerBatch = HeaderBatches(
                        exdoc = item.exdoc,
                        itemno = item.itemno,
                        batch = truckBatch.batch,
                        materialno = item.materialno,
                        parentBaseRequestedQuantity = convertUnit.convertUnit(
                            item.materialno!!,
                            truckBatch.uom!!,
                            item.uom!!,
                            truckBatch.parentBaseRequestedQuantity!!
                        ),
                        baseRequestedQuantity = convertUnit.convertUnit(
                            item.materialno!!,
                            truckBatch.uom!!,
                            item.uom!!,
                            truckBatch.baseRequestedQuantity!!
                        ),
                        baseUnit = item.uom,
                        baseCounted = 0.0,
                        requestedQuantity = convertUnit.convertUnit(
                            item.materialno!!,
                            truckBatch.uom!!,
                            item.uom!!,
                            truckBatch.requestedQuantity!!
                        ),
                        countedQuantity = 0.0,
                        expiryDate = truckBatch.expiryDate,
                        uom = item.uom,
                        bType = truckBatch.bType,
                        customizebatch = "",
                        storageLocation = item.storageLocation,
                        plant = item.plant
                    )
                        Log.d("getItem",  " ${item.materialno} , ${truckBatch.materialno} , " +
                                "${truckBatch.batch}")
                        materialsBatchesList.add(headerBatch)
                }
        return materialsBatchesList
    }


    private fun initBatchesRecyclerView(
        holder: ViewHolder,
        batches: List<HeaderBatches>,
        focused: Boolean
    ) {
        val quantity = AppUtils.round(headerItem.requestedQuantity!!).toString()
        Log.d("lasdksaldksald" , "$batches")
        batchAdapter =
            BatchListAdapter(batches, batchScanViewModel, quantity, focused, batchScanEnum)
        holder.binding.batchRV.adapter = batchAdapter
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return headerItems.size
    }

    class ViewHolder(itemBinding: ItemBatchScannerBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemBatchScannerBinding = itemBinding
    }

}