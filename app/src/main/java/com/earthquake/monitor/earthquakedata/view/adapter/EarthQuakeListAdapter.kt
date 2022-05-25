package com.earthquake.monitor.earthquakedata.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.earthquake.monitor.R
import com.earthquake.monitor.databinding.EarthquakeListItemBinding
import com.earthquake.monitor.earthquakedata.model.FeatureDTO

class EarthQuakeListAdapter(private val listItemClickListener: OnListItemClickListener) : ListAdapter<FeatureDTO, RecyclerView.ViewHolder>(FeatureDTO.DIFF_CALLBACK) {
    /** Interface definition for a listener that will be notified when a list item is clicked. */
    interface OnListItemClickListener {
        fun onListItemClicked(featureDTO: FeatureDTO)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EarthQuakeListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.earthquake_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EarthQuakeListItemViewHolder -> holder.bind(getItem(position))
        }
    }

    private inner class EarthQuakeListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = EarthquakeListItemBinding.bind(itemView)

        init {
            itemView.isEnabled = true
        }

        fun bind(featureDTO: FeatureDTO) {
            binding.placeName.text = featureDTO.properties.place
            val magnitude = featureDTO.properties.mag
            when {
                0 < magnitude && magnitude <= 0.9 -> binding.magnitude.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                0.9 < magnitude && magnitude <= 2.0 -> binding.magnitude.setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                2.0 < magnitude && magnitude <= 9.0 -> binding.magnitude.setTextColor(ContextCompat.getColor(itemView.context, R.color.burnt_orange))
                9.0 < magnitude -> binding.magnitude.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }
            binding.magnitude.text = featureDTO.properties.mag.toString()

            itemView.setOnClickListener {
                listItemClickListener.onListItemClicked(featureDTO)
            }
        }
    }
}