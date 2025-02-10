package com.company.vansales.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


object InternetCheckUtils {


    // Returns connection type. 0: none; 1: mobile data; 2: wifi; 3: VPN
    fun getConnectionType(context: Context): Int {
        var result = 0
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> result = 3
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> result = 2
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> result = 1
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    when (type) {
                        ConnectivityManager.TYPE_VPN -> result = 3
                        ConnectivityManager.TYPE_WIFI -> result = 2
                        ConnectivityManager.TYPE_MOBILE -> result = 1
                    }
                }
            }
        }
        return result
    }
}