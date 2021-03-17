package com.github.passit.data.datasource.local.model

import java.io.Serializable

data class LocationLocalData(
    var city: String,
    var state: String,
    var country: String
): Serializable