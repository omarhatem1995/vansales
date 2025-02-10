package com.company.vansales.app.activity.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.company.vansales.R
import com.company.vansales.app.utils.ViewUtils
import com.company.vansales.databinding.FilterVisitsBottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FilterVisitsBottomSheets : BottomSheetDialogFragment() {


    private lateinit var binding: FilterVisitsBottomSheetLayoutBinding
    var selectedCity: String? = null
    var selectedRegion: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.filter_visits_bottom_sheet_layout, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bdl = arguments
        val citiesList = bdl!!.getStringArrayList("cities")!!.distinct()
        val regionsList = bdl.getStringArrayList("regions")!!.distinct()

        ViewUtils.highlightSelectedValue(
            resources.getString(R.string.region),
            resources.getString(R.string.all), binding.filterVisitsRegionTV
        )

        ViewUtils.highlightSelectedValue(
            resources.getString(R.string.city),
            resources.getString(R.string.all), binding.filterVisitsCityTV
        )


        val regionPopUpMenu = PopupMenu(context, binding.filterVisitsRegionTV)
        val citiesPopUpMenu = PopupMenu(context, binding.filterVisitsCityTV)

        for (i in regionsList.indices) {
            if (regionsList[i] != "") {
                regionPopUpMenu.menu.add(Menu.NONE, i, Menu.NONE, regionsList[i])
            }
        }

        for (i in citiesList.indices) {
            if (citiesList[i] != "") {
                citiesPopUpMenu.menu.add(Menu.NONE, i, Menu.NONE, citiesList[i])
            }
        }

        binding.filterVisitsRegionTV.setOnClickListener {
            regionPopUpMenu.show()
            selectedRegion = ""
            regionPopUpMenu.setOnMenuItemClickListener { item ->
                selectedRegion = item.title.toString()
                ViewUtils.highlightSelectedValue(
                    resources.getString(R.string.region),
                    item.title as String,
                    binding. filterVisitsRegionTV
                )
                true
            }
        }

        binding.filterVisitsCityTV.setOnClickListener {
            citiesPopUpMenu.show()
            selectedCity = ""
            citiesPopUpMenu.setOnMenuItemClickListener { item ->
                selectedCity = item.title.toString()
                ViewUtils.highlightSelectedValue(
                    resources.getString(R.string.city),
                    item.title as String,
                    binding.filterVisitsCityTV
                )
                true
            }
        }

        binding.showResultsBTN.setOnClickListener {
            handleFilterResult(selectedRegion, selectedCity)
            dismiss()
        }
    }

    interface SelectedCityAndRegionListener {
        fun onShowResultClick(region: String?, city: String?)
    }

    private fun handleFilterResult(region: String?, city: String?) {
        val listener = activity as SelectedCityAndRegionListener?
        listener?.onShowResultClick(region, city)
    }

}