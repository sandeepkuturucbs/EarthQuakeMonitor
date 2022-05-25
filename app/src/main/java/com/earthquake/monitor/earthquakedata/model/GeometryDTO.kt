package com.earthquake.monitor.earthquakedata.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GeometryDTO(
    @SerializedName("type") val type: String,
    @SerializedName("coordinates") val coordinates: List<Double>
) : Parcelable
