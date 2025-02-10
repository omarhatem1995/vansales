package com.company.vansales.app.datamodel.models.localmodels

import java.math.BigDecimal


class Benefits {
     var benefitValue:BigDecimal= BigDecimal.ZERO
     var discountPercentage = 0.0
     var benefitType:String = ""
     var condition: String=""
     var description=""
 }