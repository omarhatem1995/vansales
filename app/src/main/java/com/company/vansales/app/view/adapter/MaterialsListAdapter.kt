package com.company.vansales.app.view.adapter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.localmodels.MaterialsTruckItem
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.convertToEnglish
import com.company.vansales.app.utils.AppUtils.round
import com.company.vansales.app.utils.Constants.UNIT_ALLOW_DECIMAL
import com.company.vansales.app.viewmodel.MaterialsViewModel

class MaterialsListAdapter(
    materialsList: ArrayList<MaterialsTruckItem>,
    MaterialsViewModel: MaterialsViewModel
) :
    RecyclerView.Adapter<MaterialsListAdapter.MaterialsListHolder>() {

    lateinit var context: Context
    private lateinit var lastItemHolder: MaterialsListHolder
    private val myMaterialsList: ArrayList<MaterialsTruckItem> = materialsList
    private var lastItemPosition: Int = -1
    private val materialsVM = MaterialsViewModel
    var itemSelected: ((MaterialsTruckItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialsListHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        return MaterialsListHolder(inflater.inflate(R.layout.item_material, parent, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MaterialsListHolder, position: Int) {
        val material: MaterialsTruckItem = myMaterialsList[position]
        material.selectedUnit = material.baseUnit
        holder.unit = material.selectedUnit
        holder.amount = material.unitQuantityList[holder.unit]!!
        holder.materialNameTextView.text = AppUtils.getLanguageDependencyString(
            material.materialDescrption,
            material.materialArabic,
            context.applicationContext as Application
        )
        holder.materialNumberTextView.text = material.materialNo
        holder.materialUnitPriceTextView.text =
            convertToEnglish(String.format("%.3f", material.unitPrice))
        holder.unitSelectionTextView.text = holder.unit
        if (!materialsVM.isInvoice) {
            holder.materialAvailableValue.visibility = View.GONE
            holder.materialAvailableTextView.visibility = View.GONE
        }
        material.unitQuantityList.forEach { (unit, quantity) ->
            // Check if any element in selectedMaterialsList has the same selectedUnit attribute
            var hasSelectedUnit = false
            if (materialsVM.currentMaterialList == "Item")
                hasSelectedUnit = materialsVM.selectedMaterialsList.any { it.selectedUnit == unit }
            else if (materialsVM.currentMaterialList == "Free")
                hasSelectedUnit =
                    materialsVM.selectedFreeMaterialsList.any { it.selectedUnit == unit }
            if (hasSelectedUnit) {
                // Do something if there is a matching element in selectedMaterialsList
                Log.d(
                    "onBindViewHolder",
                    "Selected unit (${unit}) is contained in the selectedMaterialsList"
                )
            } else {
                deleteRecordMaterialUnit(holder, material, unit)
            }
        }
        calcSelectedQuantity(material)
        viewChange(holder, material, true)
        Log.d(
            "viewChange", " is called onViewHolder ${material.materialNo} , " +
                    "${material.selectedUnit} , ${material.unitPrice} , ${material.selectedQuantity}"
        )
        holder.itemView.setOnClickListener {
            collapseItem(holder, material, position)
        }

        holder.materialQuantityEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                collapseItem(holder, material, position)
                true
            } else false
        }

        holder.materialQuantityEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus)
                closeKeyboard(holder)
        }
        holder.plusButton.setOnClickListener {
            setHolderAmount(holder)
            holder.amount = AppUtils.round(holder.amount + 1)
            holder.materialQuantityEditText.setText(holder.amount.toString())
            holder.materialQuantityEditText.selectAll()
        }
        holder.minusButton.setOnClickListener {
            setHolderAmount(holder)
            if (holder.amount > 0.999)
                holder.amount = AppUtils.round(holder.amount - 1)
            else
                holder.amount = 0.0
            holder.materialQuantityEditText.setText(holder.amount.toString())
            holder.materialQuantityEditText.selectAll()
        }
        holder.materialQuantityEditText.setOnClickListener { holder.materialQuantityEditText.selectAll() }

        holder.deleteImageView.setOnClickListener {
            deleteRecords(holder, material)
            collapseItem(holder, material, position)
            viewChange(holder, material)
            Log.d(
                "viewChange", " is called Delete ${material.materialNo} , " +
                        "${material.selectedUnit} , ${material.unitPrice} , ${material.selectedQuantity}"
            )
        }
        holder.unitSelectionTextView.setOnClickListener {
            setUnitPopupMenu(holder, material)
        }
    }

    private fun setUnitPopupMenu(holder: MaterialsListHolder, material: MaterialsTruckItem) {
        val popupMenu = PopupMenu(this.context, holder.unitSelectionTextView)
        var i = 1
        for ((unit, quantity) in material.unitQuantityList) {
            popupMenu.menu.add(Menu.NONE, i, Menu.NONE, "$unit : $quantity")
            i++
        }
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->
            val unitTo = item.title?.split(" : ")
            val fromUnit = material.selectedUnit
            material.unitQuantityList.forEach {
                Log.d("oiuemsx", "2 $it")
            }
            holder.amount = material.unitQuantityList[unitTo?.get(0)]!!
            holder.unit = unitTo?.get(0) ?: ""
            allowDecimal(holder)
            material.selectedUnit = unitTo?.get(0) ?: ""
            holder.materialQuantityEditText.setText(holder.amount.toString())
            holder.materialQuantityEditText.selectAll()
            holder.materialUnitPriceTextView.text = convertToEnglish(
                String.format(
                    "%.3f",
                    materialsVM.getMaterialUnitPrice(
                        material, material.selectedUnit
                    )
                )
            )
            Log.d(
                "oiuemsx", "${material.selectedUnit} , ${
                    materialsVM.getMaterialUnitPrice(
                        material, material.selectedUnit
                    )
                } , ${unitTo?.get(0)}"
            )
            Log.d(
                "viewChange", " is called popupMenu ${material.materialNo} , " +
                        "${material.selectedUnit} , ${material.unitPrice} , ${material.selectedQuantity}"
            )
            material.unitQuantityList.forEach {
                if (it.key == material.selectedUnit)
                    material.selectedQuantity = it.value
            }
            viewChange(holder, material)
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun viewChange(
        holder: MaterialsListHolder, materials: MaterialsTruckItem, fromViewHolder: Boolean? = false
    ) {
        Log.d("getViewChna", "${materials.materialNo} ${holder.unit} ${materials.selectedQuantity}")
        val dataList = materialsVM.getViewChangData(materials, holder.unit)

//        holder.materialPriceTextView.text = dataList[0].toString()
        var price = dataList[1]
        dataList.forEach {
            Log.d("getDataList", " $it")
        }

        if (materials.unitQuantityList[materials.baseUnit]!!.toDouble() <= 0.0 && fromViewHolder == true) {
            holder.materialTotalPriceTextView.text =
                convertToEnglish(String.format("%.3f", round(0.0)))
            holder.materialTotalQuantityTextView.text =
                "${materials.displayingValue} ${materials.baseUnit}"
        } else if (fromViewHolder == true) {
            holder.materialTotalPriceTextView.text =
                convertToEnglish(String.format("%.3f", round(price)))
            holder.materialTotalQuantityTextView.text =
                "${materials.unitQuantityList[materials.baseUnit]} ${holder.unit}"
        } else {
            holder.materialTotalPriceTextView.text =
                convertToEnglish(String.format("%.3f", round(price)))
            holder.materialTotalQuantityTextView.text =
                "${materials.unitQuantityList[materials.selectedUnit]} ${holder.unit}"
        }
        Log.d(
            "getSelectedQuantity12",
            "${materials.displayingValue} ${materials.selectedQuantity} ${holder.unit}"
        )
        holder.unitSelectionTextView.text = holder.unit
        holder.materialAvailableValue.text =
            "${round(dataList[3] - dataList[2])} ${holder.unit}"
        /*      if (materials.selectedQuantity > 0)
                  holder.materialImageView.setImageResource(R.drawable.ic_material_selected)
              else
                  holder.materialImageView.setImageResource(R.drawable.ic_material)*/
    }


    private fun expandItem(
        holder: MaterialsListHolder,
        materials: MaterialsTruckItem,
        position: Int
    ) {
        if (lastItemPosition != -1) {
            lastItemHolder.materialQuantityEditText.clearFocus()
        }
        materials.selectedUnit = materials.baseUnit
        holder.amount = materials.unitQuantityList[materials.selectedUnit]!!
        holder.unit = materials.baseUnit
        holder.unitSelectionTextView.text = holder.unit
        holder.materialUnitPriceTextView.text =
            convertToEnglish(String.format("%.3f", materialsVM.getMaterialBasePrice(materials)))
        holder.materialQuantityEditText.requestFocus()
        holder.materialQuantityEditText.setText(convertToEnglish(materials.displayingValue.toString()))
        holder.materialQuantityEditText.selectAll()
        allowDecimal(holder)
        showKeyboard(holder)
        lastItemHolder = holder
        lastItemPosition = position

    }

    private fun allowDecimal(holder: MaterialsListHolder) {
        if (holder.unit == UNIT_ALLOW_DECIMAL)
            holder.materialQuantityEditText.inputType =
                InputType.TYPE_NUMBER_FLAG_DECIMAL + InputType.TYPE_CLASS_NUMBER
        else
            holder.materialQuantityEditText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    private fun collapseItem(
        holder: MaterialsListHolder,
        materials: MaterialsTruckItem,
        position: Int
    ) {
        if (addedItem(holder, materials)) {
            holder.materialQuantityEditText.clearFocus()
            lastItemHolder = holder
            lastItemPosition = position
            materials.displayingValue = holder.amount
            materials.unitQuantityList.forEach {
                if (it.key == materials.selectedUnit)
                    materials.selectedQuantity = it.value
            }
            viewChange(holder, materials)
        } else {
            AppUtils.showMessage(
                this.context,
                context.resources.getString(R.string.available_Toast_message)
            )
            holder.materialQuantityEditText.requestFocus()
            holder.amount = 0.0
            holder.materialQuantityEditText.setText(convertToEnglish(holder.amount.toString()))
            holder.materialQuantityEditText.selectAll()
        }

    }


    private fun addedItem(holder: MaterialsListHolder, materials: MaterialsTruckItem): Boolean {
        val tmpAmount = materials.unitQuantityList[holder.unit]
        setHolderAmount(holder)

        materials.unitQuantityList[holder.unit] = holder.amount
        materials.displayingValue = holder.amount
        return if (calcSelectedQuantity(materials)) {
            itemSelected?.invoke(materials)
            Log.d(
                "viewChange", " is called collapseItem ${materials.materialNo} , " +
                        "${materials.selectedUnit} , ${materials.unitPrice} , ${materials.selectedQuantity}"
            )
            true
        } else {
            materials.unitQuantityList[holder.unit] = tmpAmount!!
            false
        }
    }

    private fun deleteRecords(holder: MaterialsListHolder, materials: MaterialsTruckItem) {
        holder.amount = 0.0
        holder.unit = materials.baseUnit
        holder.materialUnitPriceTextView.text = convertToEnglish(materials.unitPrice.toString())
        holder.materialQuantityEditText.setText(convertToEnglish(holder.amount.toString()))
        materials.selectedQuantity = 0.0
        materials.displayingValue = 0.0
        for ((unit, quantity) in materials.unitQuantityList) {
            if (quantity > 0) {
                materials.unitQuantityList[unit] = 0.0
                materials.selectedUnit = unit
                itemSelected?.invoke(materials)
            }
        }
        materials.selectedUnit = materials.baseUnit
    }

    private fun deleteRecordMaterialUnit(
        holder: MaterialsListHolder,
        materials: MaterialsTruckItem,
        unit: String
    ) {
        holder.amount = 0.0
        holder.unit = materials.baseUnit
        holder.materialUnitPriceTextView.text = convertToEnglish(materials.unitPrice.toString())
        holder.materialQuantityEditText.setText(convertToEnglish(holder.amount.toString()))
        materials.selectedQuantity = 0.0
        materials.displayingValue = 0.0
        materials.unitQuantityList[unit] = 0.0
        itemSelected?.invoke(materials)
        materials.selectedUnit = materials.baseUnit
    }

    private fun setHolderAmount(holder: MaterialsListHolder) {
        if (holder.materialQuantityEditText.text.isNotEmpty() &&
            holder.materialQuantityEditText.text.toString() != "."
        )
            holder.amount =
                round(
                    convertToEnglish(holder.materialQuantityEditText.text.toString())?.toDouble()
                        ?: 0.0
                )
        else
            holder.amount = 0.0
    }

    private fun calcSelectedQuantity(materials: MaterialsTruckItem): Boolean {
        var tmpQuantity = 0.0
        val baseUnit = materials.baseUnit
        for ((unit, quantity) in materials.unitQuantityList) {
            Log.d(
                "calcSelectedQuantity", " ${materials.materialNo} , " +
                        "${materials.selectedUnit} , ${materials.unitPrice} , ${materials.selectedQuantity} , " +
                        "$unit $quantity , base Unit : ${materials.baseUnit}"
            )
            var convertedQuantity = 0.0
            if (quantity > 0.0)
                convertedQuantity = materialsVM.convertToBaseWithoutRounding(
                    materials.materialNo,
                    unit,
                    baseUnit,
                    quantity
                )
            tmpQuantity += round(convertedQuantity)
        }
        if (tmpQuantity <= round(materials.available - materials.selectedFreeQuantity)
            || (!materialsVM.isInvoice)
        ) {
            materials.selectedQuantity = tmpQuantity
            Log.d("getMaterialSecl", "${materials.selectedQuantity}")
            return true
        }
        return false
    }

    private fun showKeyboard(holder: MaterialsListHolder) {
        val inputMethodManager =
            this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(holder.materialQuantityEditText, 0)
    }

    private fun closeKeyboard(holder: MaterialsListHolder) {
        val inputMethodManager =
            this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(holder.materialQuantityEditText.windowToken, 0)
    }

    class MaterialsListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val materialNameTextView: TextView =
            view.findViewById(R.id.item_material_name_textView)
        val materialQuantityEditText: EditText =
            view.findViewById(R.id.item_material_quantity_value_EditText)
        val plusButton: Button = view.findViewById(R.id.item_material_plus_button)
        val minusButton: Button = view.findViewById(R.id.item_material_minus_button)
        internal var materialAvailableValue: TextView =
            view.findViewById(R.id.item_material_available_Value)
        internal var materialAvailableTextView: TextView =
            view.findViewById(R.id.item_material_available_textView)

        /*val materialPriceTextView: TextView =
            view.findViewById(R.id.item_stock_count_batch_EditText)*/
        val materialTotalPriceTextView: TextView =
            view.findViewById(R.id.item_material_total_price_value_textView)
        val materialTotalQuantityTextView: TextView =
            view.findViewById(R.id.item_material_total_quantity_textView)
        val materialNumberTextView: TextView =
            view.findViewById(R.id.item_material_number_textView)
        val materialUnitPriceTextView: TextView =
            view.findViewById(R.id.item_material_price_value_textView)

        //        val materialImageView: ImageView = view.findViewById(R.id.item_stock_count_imageView)
        val deleteImageView: ImageView = view.findViewById(R.id.item_material_trash)
        val unitSelectionTextView: TextView =
            view.findViewById(R.id.item_material_unit_selection)
        internal var amount = 0.0
        internal var unit = ""
    }

    override fun getItemCount(): Int {
        return myMaterialsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}