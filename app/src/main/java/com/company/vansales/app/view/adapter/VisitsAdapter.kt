package com.company.vansales.app.view.adapter

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.R
import com.company.vansales.app.activity.bottomsheet.VisitOptionsBottomSheet
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.repository.VisitsRepository
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.Constants.FAILED_VISIT
import com.company.vansales.app.utils.Constants.FINISHED_VISIT
import com.company.vansales.app.utils.Constants.FROM_VISITS
import com.company.vansales.app.utils.Constants.UNFINISHED_VISIT
import com.company.vansales.app.utils.Constants.UNPLANNED_VISIT
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class VisitsAdapter(
    private val visitsList: List<Visits>
) :
    RecyclerView.Adapter<VisitsAdapter.VisitsViewHolder>() {
    lateinit var context: Context
    private lateinit var visitsRepository: VisitsRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitsViewHolder {
        context = parent.context
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        visitsRepository = VisitsRepository(context.applicationContext as Application)
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.visit_item_layout, parent, false)
        return VisitsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VisitsViewHolder, position: Int) {

        val visit = visitsList[position]
        val customer = visitsRepository.getCustomerById(visit.customerNo!!,visit.salesOrg!!,visit.dist_channel!!)
        if (visit.visitType == UNPLANNED_VISIT) {
            holder.visitType.visibility = View.VISIBLE
        }
 /*       when (visit.visitStatus) {
            UNFINISHED_VISIT -> holder.customerInitials.background =
                ContextCompat.getDrawable(context, R.drawable.unfinished_visit)
            FINISHED_VISIT -> holder.customerInitials.background =
                ContextCompat.getDrawable(context, R.drawable.finished_visit)
            FAILED_VISIT -> holder.customerInitials.background =
                ContextCompat.getDrawable(context, R.drawable.failed_visit)
        }*/
        holder.customerInitials.text = getLanguageDependencyString(
            visit.customerName,
            visit.customerNameArabic
        )[0].toString()
        holder.customerName.text =
            getLanguageDependencyString(visit.customerName, visit.customerNameArabic)
        holder.customerNumberType.text = "${customer?.telephone}  |   ${customer?.paymentTerm}"
        holder.customerAddress.text =
            getLanguageDependencyString(customer?.address!!, customer.addressArabic!!)
        holder.customerNo.text = visit.customerNo
        holder.itemView.setOnClickListener { openVisitOptionsDialog(visit) }

    }

    private fun getLanguageDependencyString(stringEnglish: String?, stringArabic: String?): String {
        return AppUtils.getLanguageDependencyString(
            stringEnglish,
            stringArabic,
            context.applicationContext as Application
        )
    }

    private fun openVisitOptionsDialog(visit: Visits) {
        val visitOptions = VisitOptionsBottomSheet()
        val b = Bundle()
        b.putParcelable("selectedVisit", visit)
        visitOptions.arguments = b
        visitOptions.show(
            (context as FragmentActivity).supportFragmentManager
            , FROM_VISITS
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return visitsList.size
    }


    class VisitsViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        val customerInitials: AppCompatTextView = view.findViewById(R.id.customerInitialize_TV)
        val customerName: TextView = view.findViewById(R.id.customerNameTV)
        val customerAddress: TextView = view.findViewById(R.id.customerAddressTV)
        val customerNumberType: TextView = view.findViewById(R.id.customerNumberTypeTV)
        val customerNo: TextView = view.findViewById(R.id.customerNoTV)
        val visitType: TextView = view.findViewById(R.id.plannedVisit_TV)
        var customerDistance: TextView = view.findViewById(R.id.customerDistanceTV)
    }

}