package com.company.vansales.app.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.company.vansales.BuildConfig
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.datamodel.models.mastermodels.Route
import com.company.vansales.app.utils.*
import com.company.vansales.app.view.fragments.DamageCheckListFragment
import com.company.vansales.app.view.fragments.RouteSelectionFragment
import com.company.vansales.app.view.fragments.SyncFragment
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.utils.extensions.displayToast
import com.company.vansales.app.view.adapter.RouteListAdapter
import com.company.vansales.app.viewmodel.ApplicationSettingsViewModel
import com.company.vansales.databinding.ActivityStartDayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartDayActivity : AppCompatActivity(), RouteListAdapter.RouteSelection {

    private lateinit var binding: ActivityStartDayBinding
    private var currentFragment: Int = 0
    private var routesBody: MutableList<Route>? = ArrayList()
    private var routes: MutableList<Routes>? = ArrayList()
    val applicationSettingsViewModel: ApplicationSettingsViewModel by viewModels()
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()!=null)
            LocaleHelper.setLocale(this, applicationSettingsViewModel.sharedPreferences.getLanguage()!!.toLowerCase()) // the code for setting your local
    }
    companion object{
        var instance : StartDayActivity ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_day)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(this, "ar")
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }

        val versionName = BuildConfig.VERSION_NAME
        binding.versionNumber.text = "Version : $versionName"

        startDamageCheckListFragment()
        initButtons()
        enableDisableNext(false)
    }

    private fun initButtons() {
        binding.startDayPreviousButton.setOnClickListener {
            onBackPressed()
        }
        binding.startDayNextButton.setOnClickListener {
            if (InternetCheckUtils.getConnectionType(this) == 0) {
                notConnected()
            } else {
                when (currentFragment) {
                    1 -> startRouteSelectionFragment()
                    2 -> {
                        startSyncFragment()
                    }
                }
            }
        }
    }

    private fun startDamageCheckListFragment() {
        currentFragment = 1
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.addToBackStack("DamageCheckListFragment")
        fragmentTransition.replace(
            R.id.start_day_container,
            DamageCheckListFragment(),
            "DamageCheckListFragment"
        )
        fragmentTransition.commitAllowingStateLoss()
    }

    private fun startRouteSelectionFragment() {
        currentFragment = 2
        enableDisableNext(enable = false)
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.addToBackStack("RouteSelectionFragment")
        fragmentTransition.replace(
            R.id.start_day_container,
            RouteSelectionFragment(),
            "RouteSelectionFragment"
        )
        fragmentTransition.commitAllowingStateLoss()
    }

    private fun startSyncFragment() {
        if (getSelectedRoutesBody().isNullOrEmpty()) {
            AppUtils.showMessage(this, resources.getString(R.string.empty_routes))
        } else {
            val bundle = Bundle()
            val syncFragment = SyncFragment()
            bundle.putString("activity",Constants.START_DAY_ACTIVITY)
            syncFragment.arguments = bundle
            currentFragment = 3
            binding.startDayNextButton.visibility = View.GONE
            binding.startDayPreviousButton.visibility = View.GONE
            val fragmentTransition = supportFragmentManager.beginTransaction()
            fragmentTransition.addToBackStack("SyncFragment")
            fragmentTransition.replace(R.id.start_day_container, syncFragment, "SyncFragment")
            fragmentTransition.commitAllowingStateLoss()
        }
    }

    override fun onBackPressed() {
        when (currentFragment) {
            1 -> finish()
            2 -> {
                currentFragment = 1
                enableDisableNext(enable = false)
                super.onBackPressed()
            }
        }
    }

    fun getSelectedRoutesBody(): List<Route>? {
        return routesBody
    }

    fun getSelectedRoutes(): List<Routes>? {
        return routes
    }

    override fun onRouteSelected(isSelected: Boolean,selectedRoute: Routes) {
        if (routesBody.isNullOrEmpty()) {
            val route = Route(selectedRoute.route, selectedRoute.salesOrg!!)
            routesBody?.add(route)
            routes!!.add(selectedRoute)
        } else {
            for (i in routesBody!!.indices) {
                if (selectedRoute.route == routesBody!![i].route) {
                    routesBody!!.remove(routesBody!![i])
                } else {
                    val route = Route(selectedRoute.route, selectedRoute.salesOrg!!)
                    routesBody!!.add(route)
                }
            }
            for (i in routes!!.indices) {
                if (selectedRoute.route == routes!![i].route && selectedRoute.driver == routes!![i].driver) {
                    routes!!.remove(routes!![i])
                } else {
                    routes!!.add(selectedRoute)
                }
            }
        }
    }

    fun enableDisableNext(enable: Boolean) {
        if (enable) {
            ViewUtils.enableView(binding.startDayNextButton)
            binding.startDayNextButton.isEnabled = true
        } else {
            ViewUtils.disableView(binding.startDayNextButton)
            binding.startDayNextButton.isEnabled = false
        }
    }

    private fun notConnected() {
        this.displayToast(this.resources.getString(R.string.disconnected_posting))
    }

}
