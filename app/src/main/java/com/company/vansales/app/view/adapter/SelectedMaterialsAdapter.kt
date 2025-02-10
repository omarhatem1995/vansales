package com.company.vansales.app.view.adapter

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.localmodels.MaterialsTruckItem
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.convertToEnglish
import com.company.vansales.app.utils.AppUtils.round
import com.company.vansales.app.viewmodel.MaterialsViewModel
import java.math.BigDecimal

class SelectedMaterialsAdapter(
    materialsList: ArrayList<MaterialsTruckItem>,
    MaterialsViewModel: MaterialsViewModel,
    ShowDeleteButton: Boolean,
    private val currentMaterialList: String? = null
) :
    RecyclerView.Adapter<SelectedMaterialsAdapter.SelectedMaterialsHolder>() {

    lateinit var context: Context
    private val myMaterialsList: ArrayList<MaterialsTruckItem> = materialsList
    private val materialsVM = MaterialsViewModel
    private val showDeleteButton = ShowDeleteButton
    var itemDelete: ((MaterialsTruckItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedMaterialsHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        return SelectedMaterialsHolder(
            inflater.inflate(
                R.layout.item_selected_material,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SelectedMaterialsHolder, position: Int) {
        val material = myMaterialsList[position]
        holder.materialNameTextView.text = AppUtils.getLanguageDependencyString(
            material.materialDescrption,
            material.materialArabic,
            context.applicationContext as Application
        )
        var materialPrice = BigDecimal.ZERO
        val materialItem = materialsVM.getMaterialFromSelectedItem(material , currentMaterialList)
        val materialItemDiscount = materialItem.discountValue
        val materialTaxes = materialItem.totalTaxes
        Log.d("getMaterialTotalPrice", "${materialsVM.getMaterialTotalPrice(material)} , " +
                "Rounded : ${materialItemDiscount}")
        materialPrice = if(materialItemDiscount.toBigDecimal() < round(materialsVM.getMaterialTotalPrice(material).toBigDecimal()))
            round(materialsVM.getMaterialTotalPrice(material).toBigDecimal() - materialItemDiscount.toBigDecimal())
        else
            round(materialsVM.getMaterialTotalPrice(material).toBigDecimal())

        holder.materialPriceTextView.text = convertToEnglish(String.format("%.3f",materialPrice))
        holder.materialQuantityTextView.text =
                "${convertToEnglish(round(material.selectedQuantity).toString())}/${material.selectedUnit}"
//            "${AppUtils.round(material.unitQuantityList[material.selectedUnit]!!)}/${material.selectedUnit}"
        holder.materialNumberTextView.text = material.materialNo
        holder.itemNumberTextView.text = material.itemNo.toString()
        holder.itemView.setOnLongClickListener {
            val popupMenu = PopupMenu(this.context, holder.itemView)
            popupMenu.menu.add(
                Menu.NONE,
                1,
                Menu.NONE,
                "Total Discount: ${round(materialItemDiscount.toBigDecimal())}"
            )
            popupMenu.menu.add(
                Menu.NONE,
                2,
                Menu.NONE,
                "Taxes: ${round(materialTaxes.toBigDecimal())}"
            )
            var i = 3
/*
            for (benefit in materialsVM.selectedMaterialsList[material.position].benefitList) {
                popupMenu.menu.add(Menu.NONE, i, Menu.NONE, benefit.description)
                i++
            }
*/
            popupMenu.show()
            true
        }
        if (materialsVM.isROffload && !showDeleteButton)
            holder.deleteMaterialImageView.visibility = View.GONE
        holder.deleteMaterialImageView.setOnClickListener {
            myMaterialsList.removeAt(position)
            itemDelete?.invoke(material)
            this.notifyDataSetChanged()

        }

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

    inner class SelectedMaterialsHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var materialNameTextView: TextView =
            view.findViewById(R.id.item_selected_material_name_textView)
        internal var materialPriceTextView: TextView =
            view.findViewById(R.id.item_selected_material_price_textView)
        internal var materialQuantityTextView: TextView =
            view.findViewById(R.id.item_selected_material_quantity_textView)
        internal val deleteMaterialImageView: ImageView =
            view.findViewById(R.id.item_selected_material_cancel_imageView)
        internal var materialNumberTextView: TextView =
            view.findViewById(R.id.item_selected_material_number_textView)
        internal val itemNumberTextView: TextView =
            view.findViewById(R.id.item_selected_item_number_textView)
    }
}
