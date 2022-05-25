package com.earthquake.monitor.earthquakedata.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetadataDTO(
    @SerializedName("generated") val generated: Long,
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String,
    @SerializedName("api") val api: String,
    @SerializedName("count") val count: Int,
    @SerializedName("status") val status: Int
) : Parcelable
