package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.mastermodels.Customer

interface GetAllCustomersUseCases {
    interface View {
        fun renderAllCustomers(data:  List<Customer>)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }

}