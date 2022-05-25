package com.earthquake.monitor.earthquakedata.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.earthquake.monitor.R
import com.earthquake.monitor.databinding.EarthquakeListFragmentBinding
import com.earthquake.monitor.earthquakedata.model.FeatureDTO
import com.earthquake.monitor.earthquakedata.view.adapter.EarthQuakeListAdapter
import com.earthquake.monitor.earthquakedata.viewmodel.EarthQuakeListViewModel
import com.example.myapplication.base.network.Status

class EarthQuakeListViewFragment : Fragment(R.layout.earthquake_list_fragment), EarthQuakeListAdapter.OnListItemClickListener {

    companion object {
        const val TAG: String = "EarthQuakeListViewFragment"
        fun newInstance() = EarthQuakeListViewFragment()
    }

    private val viewModel by activityViewModels<EarthQuakeListViewModel>()
    private var binding: EarthquakeListFragmentBinding? = null
    private val earthQuakeListAdapter: EarthQuakeListAdapter by lazy {
        EarthQuakeListAdapter(this@EarthQuakeListViewFragment)
    }

    private lateinit var refreshMenuItem: MenuItem

    // region Override Methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = EarthquakeListFragmentBinding.bind(view)
        initViews()
        addObservers()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.let { actionBar ->
            actionBar.title = requireContext().getString(R.string.lbl_summary)
            actionBar.setDisplayHomeAsUpEnabled(false)
            actionBar.setHomeButtonEnabled(true)
        }
    }

    override fun onListItemClicked(featureDTO: FeatureDTO) {
        viewModel.setSelectedFeatureDTO(featureDTO)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh_menu, menu)
        refreshMenuItem = menu.findItem(R.id.menu_item_refresh)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_refresh) {
            loadData()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    // endregion Override Methods

    // region Private Methods
    private fun initViews() {
        binding?.earthquakeList?.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = earthQuakeListAdapter
        }

        binding?.swipeToRefresh?.setOnRefreshListener {
            Log.d(TAG, "Swiped up to Refresh")
            loadData()
        }
    }

    private fun addObservers() {
        viewModel.featureDTOListLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.setShowProgress(false)
                    earthQuakeListAdapter.submitList(it.data)
                }
                Status.LOADING -> {
                    viewModel.setShowProgress(true)
                }
                Status.ERROR -> {
                    viewModel.setShowProgress(false)
                }
            }
            binding?.swipeToRefresh?.isRefreshing = false
        }
    }

    private fun loadData() {
        viewModel.getEarthQuakeList()
    }
    // endregion Private Methods
}