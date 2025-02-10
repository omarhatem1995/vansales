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
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.domain.usecases.GetRoutesUseCases
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.view.adapter.RouteListAdapter
import com.company.vansales.app.view.entities.GetRoutesViewState
import com.company.vansales.app.viewmodel.ApplicationSettingsViewModel
import com.company.vansales.app.viewmodel.RoutesViewModel
import com.company.vansales.databinding.FragmentRouteSelectionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteSelectionFragment : Fragment(), GetRoutesUseCases.View, RouteListAdapter.RouteSelection {

    private lateinit var binding: FragmentRouteSelectionBinding
    private lateinit var body: BaseBody
    private val routesViewModel: RoutesViewModel by viewModels()
    val applicationSettingsViewModel: ApplicationSettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_selection, container, false
        )
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(application, "ar")
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        callBacks()
        initButtons()
    }

    private fun initView() {
        body = BaseBody(
            "", "", "",
            AppUtils.getDriver(application)
        )
        routesViewModel.getRoutes(body)
        binding.isLoading = true
        binding.isEmpty = false
    }

    private fun callBacks() {
        routesViewModel.viewStateRoutesView.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is GetRoutesViewState.NetworkFailure -> {
                    renderLoading(false)
                    renderNetworkFailure()
                }
                is GetRoutesViewState.Loading -> {
                    renderLoading(viewState.show)
                }
                is GetRoutesViewState.RoutesData -> {
                    renderRoutesList(viewState.data)
                    renderLoading(false)
                }
            }
        }
    }

    private fun initButtons() {
   /*     binding.retryRoutesLayout.setOnClickListener {
            routesViewModel.getRoutes(body)
            binding.isLoading = true
            binding.isError = false
        }*/
    }

    private fun updateRoutesRecyclerView(routes: List<Routes>?) {
        binding.isEmpty = routes.isNullOrEmpty()
        val adapter = RouteListAdapter(routes, this)
        binding.routeList.adapter = adapter
    }

    override fun renderRoutesList(data: List<Routes>) {
        updateRoutesRecyclerView(data)
    }

    override fun renderLoading(show: Boolean) {
        binding.isLoading = show
    }

    override fun renderNetworkFailure() {
        binding.isError = true
    }

    override fun onRouteSelected(isSelected: Boolean, selectedRoute: Routes) {
        StartDayActivity.instance?.enableDisableNext(enable = true)
        StartDayActivity.instance?.onRouteSelected(isSelected, selectedRoute)
        applicationSettingsViewModel.sharedPreferences.setSelectedRoute(selectedRoute.route)
        selectedRoute.distChannel?.let {
            applicationSettingsViewModel.sharedPreferences.setDistChannel(it) }
        selectedRoute.salesOrg?.let { applicationSettingsViewModel.sharedPreferences.setSalesOrg(it) }
        selectedRoute.division?.let { applicationSettingsViewModel.sharedPreferences.setDivision(it) }
    }

}