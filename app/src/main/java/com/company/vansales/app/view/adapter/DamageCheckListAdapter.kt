package com.company.vansales.app.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.mastermodels.HelpView
import com.company.vansales.databinding.ItemDamageCheckboxBinding


class DamageCheckListAdapter(
    private val damageCheckBoxList: List<HelpView>?,
    private val damageAction: DamageAction
) : ListAdapter<HelpView,DamageCheckListAdapter.ViewHolder>(
    DiffCallback()
) {

    private var isSelectedAll: Boolean = false
    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: ItemDamageCheckboxBinding =
            DataBindingUtil.inflate(
                this.layoutInflater!!,
                R.layout.item_damage_checkbox, parent, false
            )
        return ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val damageItem = damageCheckBoxList!![position]
        holder.binding.itemDamageCheckBox.text = damageItem.filedDescrption
        holder.binding.itemDamageCheckBox.isChecked = isSelectedAll == true


        holder.binding.damageCheckboxItem.setOnClickListener {
            holder.binding.itemDamageCheckBox.isChecked = !holder.binding.itemDamageCheckBox.isChecked
            damageAction.selectDamageList(
                holder.binding.itemDamageCheckBox.isChecked,
                damageItem.fieldName
            )
        }

    }

    fun checkAllToogle(flag: Boolean){
        isSelectedAll=flag
        notifyDataSetChanged()
    }


    class ViewHolder(itemBinding: ItemDamageCheckboxBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemDamageCheckboxBinding = itemBinding
    }

    private class DiffCallback : DiffUtil.ItemCallback<HelpView>() {
        override fun areItemsTheSame(oldItem: HelpView, newItem: HelpView): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: HelpView, newItem: HelpView): Boolean {
            return oldItem == newItem
        }
    }

    interface DamageAction{
        fun selectDamageList(selected: Boolean,id: String)
    }

}