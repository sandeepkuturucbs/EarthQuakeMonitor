package com.earthquake.monitor.earthquakedata.view.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.earthquake.monitor.R
import com.earthquake.monitor.databinding.EarthquakeDetailViewFragmentBinding
import com.earthquake.monitor.earthquakedata.viewmodel.EarthQuakeListViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class EarthQuakeDetailsFragment : Fragment(R.layout.earthquake_detail_view_fragment), OnMapReadyCallback {
    companion object {
        const val TAG: String = "EarthQuakeDetailsFragment"
        fun newInstance() = EarthQuakeDetailsFragment()
    }

    private var binding: EarthquakeDetailViewFragmentBinding? = null
    private val viewModel by activityViewModels<EarthQuakeListViewModel>()
    private var googleMap: GoogleMap? = null

    //endregion Override Methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = EarthquakeDetailViewFragmentBinding.bind(view)
        initViews()
        addObservers()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.let { actionBar ->
            actionBar.title = requireContext().getString(R.string.lbl_detail_view)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.menu_item_refresh)
        if (item != null) {
            item.isVisible = false
            item.isEnabled = false
        }
        super.onPrepareOptionsMenu(menu)
    }
    //endregion Override Methods

    // region Private Methods
    private fun initViews() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun addObservers() {
        viewModel.featureDTOLiveData.observe(viewLifecycleOwner) {
            loadData()
        }
    }

    private fun loadData() {
        updateGoogleMap()
        viewModel.featureDTOLiveData.value?.let { feature ->

            val magnitude = feature.properties.mag
            @ColorRes val magnitudeColor: Int = when {
                0 < magnitude && magnitude <= 0.9 -> R.color.green
                0.9 < magnitude && magnitude <= 2.0 -> R.color.orange
                2.0 < magnitude && magnitude <= 9.0 -> R.color.burnt_orange
                9.0 < magnitude -> R.color.red
                else -> R.color.green
            }
            binding?.magnitudeValue?.text = magnitude.toString()
            binding?.magnitudeValue?.setTextColor(ContextCompat.getColor(requireContext(), magnitudeColor))

            val date = Date(feature.properties.time)
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            binding?.dateValue?.text = formatter.format(date)

            val ldt: LocalDateTime = Instant.ofEpochMilli(feature.properties.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val zonedDateTime: ZonedDateTime = ZonedDateTime.of(ldt, ZoneId.systemDefault())
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss z")
            binding?.timeValue?.text = zonedDateTime.format(timeFormatter)

            val parseValue: Array<String> = feature.properties.title.split("(?<=of)".toRegex()).toTypedArray()
            if (parseValue.size >= 2) {
                binding?.placeValue?.text = parseValue[1]
            } else {
                binding?.groupPlace?.visibility = View.GONE
            }

            if (feature.geometry.coordinates.size >= 3) {
                binding?.depthValue?.text = feature.geometry.coordinates[2].toString()
            } else {
                binding?.groupDepth?.visibility = View.GONE
            }
        }
    }

    private fun updateGoogleMap() {
        googleMap?.let { gMap ->
            viewModel.featureDTOLiveData.value?.let { feature ->
                if (feature.geometry.coordinates.size >= 3) {
                    val longitude = feature.geometry.coordinates[0]
                    val latitude = feature.geometry.coordinates[1]
                    val parseValue: Array<String> = feature.properties.title.split("(?<=of)".toRegex()).toTypedArray()
                    var cityName: String? = null
                    if (parseValue.size >= 2) {
                        cityName = parseValue[1]
                    }
                    // Add the marker city, country
                    // and move the map camera to the same location
                    val location = LatLng(latitude, longitude)
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 5f))
                    gMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(cityName)
                            .snippet("${getString(R.string.label_magnitude)}:${feature.properties.mag}")
                    )
                }
            }
        }
    }
    // endregion Private Methods
}