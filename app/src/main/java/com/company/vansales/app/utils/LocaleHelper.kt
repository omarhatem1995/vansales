package com.company.vansales.app.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.core.os.ConfigurationCompat
import androidx.preference.PreferenceManager
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun setLocale(context: Context, language: String?): Context {
        persist(context, language)
        return updateResources(context, language)
    }

    private fun persist(context: Context, language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }


    private fun updateResources(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    const val OPTION_PHONE_LANGUAGE = "sys_def"
    val supportedLocales = listOf("en", "ar")

    fun getLocaleFromPrefCode(prefCode: String): Locale {
        val localeCode = if(prefCode != OPTION_PHONE_LANGUAGE) {
            prefCode
        } else {
            val systemLang = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)?.language
            if(systemLang in supportedLocales){
                systemLang
            } else {
                "en"
            }
        }
        return Locale(localeCode)
    }

    @Suppress("DEPRECATION")
    private fun getLocaleFromConfiguration(configuration: Configuration): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            configuration.locales.get(0)
        } else {
            configuration.locale
        }
    }

    fun getLocalizedConfiguration(locale: Locale): Configuration {
        val config = Configuration()
        return config.apply {
            config.setLayoutDirection(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                config.setLocale(locale)
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                config.setLocales(localeList)
            } else {
                config.setLocale(locale)
            }
        }
    }

    fun applyLocalizedContext(baseContext: Context, prefLocaleCode: String) {
        val currentLocale = getLocaleFromPrefCode(prefLocaleCode)
        val baseLocale = getLocaleFromConfiguration(baseContext.resources.configuration)
        Locale.setDefault(currentLocale)
        if (!baseLocale.toString().equals(currentLocale.toString(), ignoreCase = true)) {
            val config = getLocalizedConfiguration(currentLocale)
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }
    }
}