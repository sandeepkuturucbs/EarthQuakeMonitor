package com.earthquake.monitor.earthquakedata.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeatureDTO(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("properties") val properties: PropertiesDTO,
    @SerializedName("geometry") val geometry: GeometryDTO
) : Parcelable {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FeatureDTO>() {
            override fun areItemsTheSame(oldItem: FeatureDTO, newItem: FeatureDTO): Boolean {
                // TODO: Improve it
                return oldItem.id == newItem.id &&
                    oldItem.type == newItem.type &&
                    oldItem.properties == newItem.properties &&
                    oldItem.geometry == newItem.geometry
            }

            override fun areContentsTheSame(oldItem: FeatureDTO, newItem: FeatureDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}
