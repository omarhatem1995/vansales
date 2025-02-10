package com.company.vansales.app.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.company.vansales.R
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.activity.StartDayActivity
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.HelpView
import com.company.vansales.app.domain.usecases.GetHelpUseCases
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.view.adapter.DamageCheckListAdapter
import com.company.vansales.app.view.entities.GetHelpViewState
import com.company.vansales.app.viewmodel.HelpViewViewModel
import com.company.vansales.databinding.FragmentDamageCheckListBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DamageCheckListFragment : Fragment(),
    DamageCheckListAdapter.DamageAction,
    GetHelpUseCases.View {

    private lateinit var binding: FragmentDamageCheckListBinding
    private lateinit var body: BaseBody
    private val helpViewViewViewModel: HelpViewViewModel by viewModels()
    private lateinit var adapter: DamageCheckListAdapter
    private var damagesList: List<HelpView>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_damage_check_list, container, false
        )
        if(helpViewViewViewModel.sharedPreferences.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(application, "ar")
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initButtons()
    }

    private fun initView() {
        body = BaseBody(
            "1000", "", "", AppUtils.getDriver(application)
        )
        helpViewViewViewModel.getHelp(body)
        initObserver()
        StartDayActivity.instance?.enableDisableNext(true)
    }

    private fun initButtons() {
  /*      binding.retryDamageCheckListLayout.setOnClickListener {
            helpViewViewViewModel.getHelp(body)
            initObserver()
        }*/

        /*  //this line is if we want to make them checkall again
          binding.ivCheckAll.setOnClickListener {
              if (this::adapter.isInitialized) {
              checkAll = !checkAll
                  adapter.checkAllToogle(checkAll)
                  StartDayActivity.instance?.enableDisableNext(checkAll)
              }
          }*/
    }

    private fun initObserver() {
        helpViewViewViewModel.viewStateHelpView.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is GetHelpViewState.NetworkFailure -> {
                    renderLoading(false)
                    renderNetworkFailure()
                }
                is GetHelpViewState.Loading -> {
                    renderLoading(viewState.show)
                }
                is GetHelpViewState.FilteredDataByLanguage -> {
                    renderHelpList(viewState.data)
                    renderLoading(false)
                }

                is GetHelpViewState.Data -> {
                    renderHelpList(viewState.data.data)
                    renderLoading(false)
                }
            }
        }
    }

    private fun updateDamageListRecyclerView(data: List<HelpView>) {
        damagesList = data
        adapter = DamageCheckListAdapter(data, this)
        binding.damageCheckBoxRV.adapter = adapter
        adapter.submitList(data)
    }

    override fun renderHelpList(data: List<HelpView>) {
        updateDamageListRecyclerView(data)
    }

    override fun renderLoading(show: Boolean) {
        binding.isLoading = show
    }

    override fun renderNetworkFailure() {}

    override fun selectDamageList(selected: Boolean, id: String) {
        //nth to handle
    }
}