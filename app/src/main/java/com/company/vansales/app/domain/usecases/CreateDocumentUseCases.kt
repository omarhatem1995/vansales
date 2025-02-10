package com.company.vansales.app.domain.usecases

import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse

interface CreateDocumentUseCases {

    interface View {
        fun renderCreateDocument(data : BaseResponse<PostDocumentResponse>)
        fun renderCreateDocument(salesDocBody: SalesDocBody,data : BaseResponse<PostDocumentResponse> , name :String , number :String)
        fun renderCreateDocument(data : BaseResponse<PostDocumentResponse>, iSalesDocHeader: ISalesDocHeader,
                                 headerItems: ArrayList<HeaderItems>,
                                 headerBatches: ArrayList<HeaderBatches>)
        fun renderLoading(show:Boolean)
        fun renderNetworkFailure()
        fun renderUnknownError()
        fun renderEmptyResponse()
        fun renderCreateDocumentError()
    }
    interface CreateDocumentInvoke {
        suspend fun invoke(salesDocBody: SalesDocBody) :
                ApiResponse<BaseResponse<PostDocumentResponse>, Error>
    }
    }