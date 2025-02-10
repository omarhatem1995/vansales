package com.company.vansales.app.datamodel.services.interceptor

import com.company.vansales.app.SAPWizardApplication
import com.company.vansales.app.utils.AppUtils
//import com.company.vansales.app.view.App
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.URISyntaxException
import javax.inject.Singleton

@Singleton
class DomainURLInterceptor : Interceptor {

    @Volatile private var host: HttpUrl? = null

    fun initHost(host: String, port: String) {
        this.host = "http://$host:$port/".toHttpUrlOrNull()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var newUrl: HttpUrl? = null
        if (host == null)
            host = AppUtils.urlBuilder(SAPWizardApplication.application).toHttpUrlOrNull()
        try {
            host?.let {
                newUrl = request.url.newBuilder()
                    .scheme(it.scheme)
                    .host(it.toUrl().toURI().host)
                    .port(it.port)
                    .build()
            }

        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        newUrl?.let {
            request = request.newBuilder()
                .url(it)
                .build()
        }
        return chain.proceed(request)
    }
}