package com.company.vansales.app.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.R
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.localmodels.MaterialsTruckItem
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.view.adapter.SelectedMaterialsAdapter
import com.company.vansales.app.viewmodel.MaterialsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectedMaterialsListFragment (
    materialsList: ArrayList<MaterialsTruckItem>,
    materialsViewModel: MaterialsViewModel,
    showDeleteButton: Boolean,
    currentMaterialList : String? = null
) : Fragment() {

    private val myMaterialsList = materialsList
    private val materialsVM = materialsViewModel
    val adapter = SelectedMaterialsAdapter(myMaterialsList, materialsVM, showDeleteButton , currentMaterialList)
    var rvOnTouch: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selected_materials_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(materialsVM.sharedPreference.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(application, "ar")
        }
        val rv = view.findViewById<RecyclerView>(R.id.fragment_selected_materials_list)
        rv.adapter = adapter
        rv.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN)
                rvOnTouch?.invoke("DOWN")
            else if (event.action == MotionEvent.ACTION_UP)
                rvOnTouch?.invoke("UP")
            false
        }
        materialsVM.applyDiscount()
    }

}
