package com.highenergymind.utils

import android.content.Context
import android.os.Build
import java.util.Locale

/**
 * Created by ParasMobile on 3/15/2018.
 */
object LocaleHelper {
    fun setLocale(context: Context, language: String?): Context {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else updateResourcesLegacy(context, language)
    }

    private fun updateResources(context: Context, language: String?): Context {
        val locale: Locale = if (language != null && language.contains("-")) {
            val languageCode = language.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            Locale(languageCode[0], languageCode[1].replace("r", ""))
        } else {
            Locale(language)
        }
        //        Locale.setDefault(locale);
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    @Suppress("deprecation")
    private fun updateResourcesLegacy(context: Context, language: String?): Context {
        val locale: Locale = if (language != null && language.contains("-")) {
            val languageCode = language.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            Locale(languageCode[0], languageCode[1].replace("r", ""))
        } else {
            Locale(language)
        }
        //        Locale.setDefault(locale);
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}