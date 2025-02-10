package com.company.vansales.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.mastermodels.Routes


class RouteListAdapter(
    private var routeList: List<Routes>?,
    private var routeSelection: RouteSelection
) :
    RecyclerView.Adapter<RouteListAdapter.RouteHolder>() {

    lateinit var context: Context
    var selectionPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        return RouteHolder(inflater.inflate(R.layout.item_route, parent, false))
    }


    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        val route = routeList!![position]
        holder.routeTextView.text = route.route
        holder.selectedRouteCheckBox.isChecked = position == selectionPosition
        holder.selectedRouteCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isDirty && b) {
                notifyItemChanged(selectionPosition)
                selectionPosition = position
                routeSelection.onRouteSelected(
                    holder.selectedRouteCheckBox.isChecked,
                    route
                )
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class RouteHolder(view: View) : RecyclerView.ViewHolder(view) {
        val routeTextView: TextView = view.findViewById(R.id.item_route_textView)
        val routeItem: ConstraintLayout = view.findViewById(R.id.route_item)
        val selectedRouteCheckBox: AppCompatCheckBox =
            view.findViewById(R.id.item_route_imageView)
    }

    override fun getItemCount(): Int {
        return routeList!!.size
    }

    interface RouteSelection {
        fun onRouteSelected(isSelected: Boolean, selectedRoute: Routes)
    }
}