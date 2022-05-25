package com.earthquake.monitor.earthquakedata.view.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.earthquake.monitor.R
import com.earthquake.monitor.base.ui.addFragment
import com.earthquake.monitor.base.ui.replaceFragment
import com.earthquake.monitor.databinding.EarthquakeListActivityMainBinding
import com.earthquake.monitor.earthquakedata.view.fragment.EarthQuakeDetailsFragment
import com.earthquake.monitor.earthquakedata.view.fragment.EarthQuakeListViewFragment
import com.earthquake.monitor.earthquakedata.viewmodel.EarthQuakeListViewModel

class EarthQuakeListActivity : AppCompatActivity() {
    private val viewModel: EarthQuakeListViewModel by viewModels()
    private var binding: EarthquakeListActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EarthquakeListActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        addObservers()
    }

    private fun addObservers() {
        addFragmentObserver()
        addProgressbarObserver()
    }

    private fun addFragmentObserver() {
        viewModel.replaceFragmentLiveData.observe(this) { fragmentTag ->
            when (fragmentTag) {
                EarthQuakeListViewFragment.TAG -> {
                    addFragment(EarthQuakeListViewFragment.newInstance(), R.id.content_frame)
                }
                EarthQuakeDetailsFragment.TAG -> {
                    // Don't navigate to EarthQuakeDetailsFragment if featureDTOLiveData is not set
                    viewModel.featureDTOLiveData.value?.let { _ ->
                        replaceFragment(EarthQuakeDetailsFragment.newInstance(), R.id.content_frame, true)
                    }
                }
            }
        }
    }

    private fun addProgressbarObserver() {
        viewModel.showProgressLiveData.observe(this) { showProgressBar ->
            when {
                showProgressBar -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
                else -> {
                    binding?.progressBar?.visibility = View.GONE
                }
            }
        }
    }
}