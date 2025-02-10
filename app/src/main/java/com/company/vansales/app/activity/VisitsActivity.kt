package com.company.vansales.app.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.company.vansales.BuildConfig
import com.company.vansales.R
import com.company.vansales.app.activity.bottomsheet.FilterVisitsBottomSheets
import com.company.vansales.app.datamodel.models.mastermodels.OpenInvoicesRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.Constants
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.view.adapter.VisitsAdapter
import com.company.vansales.app.viewmodel.ApplicationSettingsViewModel
import com.company.vansales.app.viewmodel.VisitsViewModel
import com.company.vansales.databinding.ActivityVisitsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitsActivity : AppCompatActivity(),
    View.OnClickListener,
    FilterVisitsBottomSheets.SelectedCityAndRegionListener {

    private lateinit var binding: ActivityVisitsBinding
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val visitViewModel: VisitsViewModel by viewModels()
    private val applicationSettingsViewModel: ApplicationSettingsViewModel by viewModels()
    private lateinit var visitsList: List<Visits>
    private var filteredList: List<Visits>? = null
    private var routesList: List<String>? = null
    private var selectedRoute: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_visits)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(this, "ar")
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }
        initButtons()
        initView()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()!=null)
        LocaleHelper.setLocale(this, applicationSettingsViewModel.sharedPreferences.getLanguage()!!.toLowerCase()) // the code for setting your local
    }
    private fun initView() {
        val versionName = BuildConfig.VERSION_NAME
        binding.versionNumber.text = "Version : $versionName"
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        routesList = visitViewModel.getRoutesList()
        selectedRoute = applicationSettingsViewModel.sharedPreferences.getSelectedRoute()
        if (selectedRoute.isNullOrEmpty()) {
            emptyVisitsView()
        } else {
            getAllVisitsAscending(selectedRoute!!)
            initViews()
        }
        visitViewModel.getTruckContents()
    }

    private fun initButtons() {
        binding.visitsToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initViews() {
        initVisitsList()
        binding.filterVisitsByDistanceIV.setOnClickListener(this)
        binding.sortVisitsAscDescIV.setOnClickListener(this)
        binding.toCustomersListFAB.setOnClickListener(this)
        binding.toCreateCustomerFAB.setOnClickListener(this)
        binding.toDriverCollectionsFAB.setOnClickListener(this)
        binding.filterVisitsByIV.setOnClickListener(this)
        binding.routeSelectionIV.setOnClickListener(this)
        binding.menuRed.setClosedOnTouchOutside(true)
        binding.visitsRV.setHasFixedSize(true)
        binding.routeSelectionIV.text = selectedRoute
        binding.searchVisitsET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                searchQuery: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (searchQuery.isNotEmpty()) {
                    val searchResultList = handleSearch(searchQuery.toString())
                    updateVisitsList(searchResultList)
                } else {
                    if (!filteredList.isNullOrEmpty()) {
                        updateVisitsList(filteredList!!)
                    } else {
                        getAllVisitsAscending(selectedRoute!!)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.routeSelectionIV -> initSortByRoute()
            R.id.filterVisitsByDistanceIV
            -> initFilterByStatusMenu()
            R.id.sortVisitsAscDescIV -> initSortAscOrDescMenu()
            R.id.filterVisitsBy_IV -> initFilterVisitsByCityAndRegion()
            R.id.toCustomersListFAB ->{} /*startActivity(
                Intent(
                    applicationContext,
                    CustomersActivity::class.java
                )
            )*/
            R.id.toDriverCollectionsFAB -> {
             /*   val intent = Intent(applicationContext, CollectionActivity()::class.java)
                val openInvoicesRequestModel = OpenInvoicesRequestModel(
                    customer = AppUtils.getDriver(application),
                    salesOrg = applicationSettingsViewModel.sharedPreferences.getSalesOrg()!!,
                    distChannel = applicationSettingsViewModel.sharedPreferences.getDistChannel()!!,
                    division = applicationSettingsViewModel.sharedPreferences.getDivision()!!
                )
                intent.putExtra("customer", openInvoicesRequestModel)
                startActivity(intent)*/
            }
            R.id.toCreateCustomerFAB -> {}/*startActivity(
                Intent(
                    applicationContext,
                    CreateCustomerActivity::class.java
                )
            )*/

        }
    }

    private fun getAllVisitsAscending(route: String) {
        visitViewModel.getAllVisitsByRouteAscending(route)
            .observe(this) { visits ->
                if (!visits.isNullOrEmpty()) {
                    initViews()
                    updateVisitsList(visits)
                    visitsList = visits
                } else {
                    emptyVisitsView()
                }
            }
    }

    private fun getAllVisitsDescending(route: String) {
        visitViewModel.getAllVisitsByRouteDescending(route)
            .observe(this) { visits ->
                updateVisitsList(visits!!)
            }
    }

    private fun initFilterVisitsByCityAndRegion() {
        val filterVisits = FilterVisitsBottomSheets()
        val b = Bundle()
        b.putStringArrayList("cities", visitViewModel.citiesList())
        b.putStringArrayList("regions", visitViewModel.regionsList())
        filterVisits.arguments = b
        filterVisits.show(
            (this as FragmentActivity).supportFragmentManager, Constants.FROM_VISITS
        )
    }

    override fun onShowResultClick(region: String?, city: String?) {
        if (handleFilterByRegionAndCity(region, city).isEmpty()) {
            getAllVisitsAscending(selectedRoute!!)
        } else {
            updateVisitsList(handleFilterByRegionAndCity(region, city))
        }
    }

    private fun updateVisitsList(visits: List<Visits>) {
        binding.isEmpty = visits.isNullOrEmpty()
        val visitsAdapter = VisitsAdapter(visits)
        binding.visitsRV.adapter = visitsAdapter
        visitsAdapter.notifyDataSetChanged()
    }

    private fun initFilterByStatusMenu() {
        val filterByStatusMenu: PopupMenu?
        filterByStatusMenu = PopupMenu(applicationContext, binding.filterVisitsByDistanceIV)
        filterByStatusMenu.inflate(R.menu.menu_show_visits_by)
        filterByStatusMenu.show()
        filterByStatusMenu.setOnMenuItemClickListener { item ->
            if (item.toString() == resources.getString(R.string.all)) {
                getAllVisitsAscending(selectedRoute!!)
            } else if (item.toString() == resources.getString(R.string.completed)) {
                updateVisitsList(visitViewModel.getFinishedVisits())
            }
            true
        }
    }

    private fun initSortAscOrDescMenu() {
        val sortAscOrDescMenu: PopupMenu?
        sortAscOrDescMenu = PopupMenu(applicationContext, binding.sortVisitsAscDescIV)
        sortAscOrDescMenu.inflate(R.menu.menu_sort_visits_by)
        sortAscOrDescMenu.show()
        sortAscOrDescMenu.setOnMenuItemClickListener { item ->
            if (item.toString() == resources.getString(R.string.ascending)) {
                getAllVisitsAscending(selectedRoute!!)
            } else {
                getAllVisitsDescending(selectedRoute!!)
            }
            true
        }
    }

    private fun initSortByRoute() {
        val routesPopUpMenu = PopupMenu(this, binding.routeSelectionIV)
        if (!selectedRoute.isNullOrEmpty()) {
            for (i in routesList!!.indices) {
                if (routesList!![i] != "") {
                    routesPopUpMenu.menu.add(Menu.NONE, i, Menu.NONE, routesList!![i])
                }
            }
            routesPopUpMenu.show()
            routesPopUpMenu.setOnMenuItemClickListener { route ->
                selectedRoute = route.toString()
                binding.routeSelectionIV.text = route.title
                getAllVisitsAscending(route.toString())
                true
            }
        }
    }

    private fun handleSearch(searchQuery: String): List<Visits> {
        return if (!filteredList.isNullOrEmpty()) {
            filteredList!!.filter { filter ->
                filter.customerName!!.contains(searchQuery, true) ||
                        filter.customerNameArabic!!.contains(searchQuery, true) ||
                        filter.customerNo!!.contains(searchQuery,true)
            }.toList()
        } else {
            visitsList.filter { filter ->
                filter.customerName!!.contains(searchQuery, true) ||
                        filter.customerNameArabic!!.contains(searchQuery, true)||
                        filter.customerNo!!.contains(searchQuery,true)
            }
                .toList()
        }
    }

    private fun handleFilterByRegionAndCity(region: String?, city: String?): List<Visits> {
        val regionCQ: CharSequence = region.toString()
        val cityCQ: CharSequence = city.toString()
        filteredList = visitsList.filter { p ->
            p.customerRegion!!.contains(regionCQ, true) || p.customerCity!!.contains(cityCQ, true)
        }
            .toList()
        return filteredList as List<Visits>
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i(TAG, "User interaction was cancelled.")
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) ->
                    if (!selectedRoute.isNullOrEmpty()) {
                        getAllVisitsAscending(
                            selectedRoute!!
                        )
                    } else {
                        emptyVisitsView()
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkLocation()
    }

    private fun checkLocation() {
   /*     val locationPermission = LocationPermission(this)
        if (!locationPermission.checkLocationPermission()) {
            locationPermission.requestLocationPermission()
        }*/
    }

    private fun emptyVisitsView() {
        binding.isEmpty = true
    }

    private fun initVisitsList() {
        binding.isEmpty = false
    }


}



