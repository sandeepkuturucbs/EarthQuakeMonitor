package com.earthquake.monitor.earthquakedata.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeatureCollectionDTO(
    @SerializedName("type") val type: String,
    @SerializedName("metadata") val metadata: MetadataDTO,
    @SerializedName("bbox") val bbox: List<Double>,
    @SerializedName("features") val features: List<FeatureDTO>
) : Parcelable
