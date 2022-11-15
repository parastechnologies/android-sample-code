package com.app.muselink.listener

import com.ehsanmashhadi.library.model.Country

interface CountrySelectionNavigator {
        fun onSelectCountry(country: Country?)
    }