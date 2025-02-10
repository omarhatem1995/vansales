package com.company.vansales.app.activity.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.company.vansales.R
import com.company.vansales.app.activity.ActionsActivity
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.repository.CustomerRepository
import com.company.vansales.app.datamodel.repository.VisitsRepository
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.Constants.FAILED_VISIT
import com.company.vansales.app.utils.Constants.FINISHED_VISIT
import com.company.vansales.app.utils.Constants.FROM_VISITS
import com.company.vansales.app.viewmodel.VisitsViewModel
import com.company.vansales.databinding.VisitOptionsBottomSheetLayoutBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VisitOptionsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: VisitOptionsBottomSheetLayoutBinding
    private lateinit var visitRepository: VisitsRepository
    private lateinit var customerRepository: CustomerRepository
    val visitViewModel: VisitsViewModel by viewModels()
    private lateinit var selectedVisit: Visits
    private lateinit var selectedCustomer: Customer
    lateinit var bundle: Bundle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.visit_options_bottom_sheet_layout, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        visitRepository = VisitsRepository(this.requireActivity().application)
        customerRepository = CustomerRepository(this.requireActivity().application)
        if (tag.toString() == FROM_VISITS) {
            fromVisits(bundle!!)
        } else {
            fromCustomers(bundle!!)
        }
        getUpdateLocation()
    }

    private fun fromCustomers(bundle: Bundle) {
        val customer = bundle.getParcelable<Customer>("selectedCustomer")
        if (customer != null) {
            selectedCustomer = customer
            binding.markAsFinishedTV.visibility = View.GONE
            binding.view10.visibility = View.GONE
            binding.customerNameVisitTV.text = selectedCustomer.name1
         /*   binding.showCustomerLocationTV.setOnClickListener {
                showLocation(selectedCustomer.latitude!!, selectedCustomer.longitude!!)
            }*/
            binding.startOptionTV.setOnClickListener {
                if (visitRepository.isAVisitCurrentlyInProgress()) {
                    alreadyAnActiveVisitExists()
                } else {
                    createAndStartAVisit(selectedCustomer)
                }
            }
        }else{
            dismiss()
        }
    }

    private fun fromVisits(bundle: Bundle) {
        val visit = bundle.getParcelable<Visits>("selectedVisit")
        selectedVisit = visit!!
        binding.customerNameVisitTV.text = selectedVisit.customerName
        binding.showCustomerLocationTV.setOnClickListener {
         /*   showLocation(
                getCostumer(
                    selectedVisit.customerNo!!,
                    selectedVisit.salesOrg!!,
                    selectedVisit.dist_channel!!
                )?.latitude!!,
                getCostumer(
                    selectedVisit.customerNo!!,
                    selectedVisit.salesOrg!!,
                    selectedVisit.dist_channel!!
                )?.longitude!!
            )*/
        }
        if (selectedVisit.visitStatus == FINISHED_VISIT || selectedVisit.visitStatus == FAILED_VISIT) {
            selectedVisitFinishedOrFailed()
        }
        binding.startOptionTV.setOnClickListener {
            if (visitRepository.isAVisitCurrentlyInProgress()) {
                alreadyAnActiveVisitExists()
            } else {
                startVisit()
            }
        }
        binding.markAsFinishedTV.setOnClickListener {
            markAsFinished()
            dismiss()
        }
    }

    private fun createAndStartAVisit(customer: Customer) {
        val lat = customer.latitude ?: 0.0
        val lng = customer.longitude ?: 0.0


      /*  if (acurateLocation?.getLatLng() == null) {
            requireContext().displayToast(getString(R.string.wait_loc))
        } else {
            if (isInsideLocation(
                    currLoc = acurateLocation?.getLatLng()!!,
                    destinationLoc = LatLng(lat, lng)
                )
            ) {
                customerRepository.createAndStartAVisit(
                    this.requireActivity().application,
                    visitViewModel.getRoutesList()[0],
                    customer , acurateLocation?.latitude , acurateLocation?.longitude
                )*/
                startActivity(Intent(this.requireContext(), ActionsActivity::class.java))
                this.requireActivity().finish()
                dismiss()
        /*    } else {
                requireContext().displayToast(getString(R.string.you_are_not_area))
            }
        }*/
    }


    private fun startVisit() {
        val customer = getCostumer(
            selectedVisit.customerNo!!,
            selectedVisit.salesOrg!!,
            selectedVisit.dist_channel!!
        )
       /* val lat = customer?.latitude!!
        val lng = customer?.longitude!!
        if (requireContext().isLocationEnabled()) {
            if (acurateLocation?.getLatLng() == null) {
                requireContext().displayToast(getString(R.string.wait_loc))
            } else {
                if (customer.paymentTerm == USER_BLOCKED) {
                    val blockedUserFragment = BlockedUserDialog(requireContext(),true)
                    blockedUserFragment.show()
                } else {

                    if (isInsideLocation(
                            currLoc = acurateLocation?.getLatLng()!!,
                            destinationLoc = LatLng(lat, lng)
                        )
                    ) {*/
                        if (selectedVisit.visitStatus != FINISHED_VISIT && selectedVisit.visitStatus != FAILED_VISIT) {
                            visitViewModel.updateStartVisitData(
                                selectedVisit.visitListID,
                                selectedVisit.visitItemNo,
                                AppUtils.getFormatForEndDay(),
                                AppUtils.getFormatForEndDay(),
                                0.0,
                                0.0
                            )
                        }
                        visitRepository.startVisit(
                            selectedVisit.visitListID,
                            selectedVisit.visitItemNo
                        )
                        startActivity(Intent(this.requireContext(), ActionsActivity::class.java))
                        dismiss()
                   /* } else {
                        requireContext().displayToast(getString(R.string.you_are_not_area))
                    }
                }
            }*/

        }
        /*else{
            requireContext().displayToast(getString(R.string.please_open_gps))
        }*/


  fun finishVisit() {
      visitRepository.finishCurrentVisit()
    }

    private fun alreadyAnActiveVisitExists() {
        finishVisit()
        /*AppUtils.showMessage(
            this.requireActivity(),
            resources.getString(R.string.visit_in_progress)
        )*/
        createAndStartAVisit(selectedCustomer)

        dismiss()
    }

    private fun selectedVisitFinishedOrFailed() {
        binding.startOptionTV.text = resources.getString(R.string.start_again)
        binding.markAsFinishedTV.visibility = View.GONE
        binding.view10.visibility = View.GONE
    }

    private fun getCostumer(costumerNo: String, salesOrg: String, distChannel: String): Customer? {
        return visitRepository.getCustomerById(costumerNo, salesOrg, distChannel)
    }

/*    private fun showLocation(latitude: Double, longitude: Double) {
        this.dismiss()
        AppUtils.openGoogleMap(
            latitude,
            longitude,
            this.requireContext()
        )
    }*/

    private fun markAsFinished() {
        val lat = getCostumer(
            selectedVisit.customerNo!!,
            selectedVisit.salesOrg!!,
            selectedVisit.dist_channel!!
        )?.latitude!!
        val lng = getCostumer(
            selectedVisit.customerNo!!,
            selectedVisit.salesOrg!!,
            selectedVisit.dist_channel!!
        )?.longitude!!
       /* if (requireContext().isLocationEnabled()) {
            if (acurateLocation?.getLatLng() == null) {
                requireContext().displayToast(getString(R.string.wait_loc))
            } else {
                if (isInsideLocation(
                        currLoc = acurateLocation?.getLatLng()!!,
                        destinationLoc = LatLng(lat, lng)
                    )
                ) {
                    ViewUtils.showVisitFailureReasonDialog(
                        false,
                        visitRepository,
                        selectedVisit,
                        this.activity as Activity,
                        acurateLocation?.latitude ?: 0.0,
                        acurateLocation?.longitude ?: 0.0
                    )
                }
            }
        }*/
    }

    /**
     * TODO for testing add !
     * just remove --!-- and it will work fine
     * because we don't have now loc beside us in cairo
     */
    private fun isInsideLocation(currLoc: LatLng, destinationLoc: LatLng): Boolean {
     /*   return !PolyUtil.isLocationOnPath(
            currLoc,
            mutableListOf(destinationLoc),
            true,
            Constants.TOLERANCE
        )*/
        return true
    }

    private fun getUpdateLocation() {
    /*    UpdateLocationService.locationResult.observe(this) {
            if (it.gpsEnable)
                acurateLocation = it.acurateLocation
        }*/
    }

}
