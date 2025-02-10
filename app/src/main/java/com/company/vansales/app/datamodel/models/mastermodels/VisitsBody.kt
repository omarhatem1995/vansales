package com.company.vansales.app.datamodel.models.mastermodels
data class VisitsBody(
    var input: BaseBody,
    var routes: List<Route>?
)

data class Route(
    var route: String,
    var salesOrg: String
)