package com.earthquake.monitor.earthquakedata.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earthquake.monitor.base.network.UIResource
import com.earthquake.monitor.earthquakedata.model.FeatureDTO
import com.earthquake.monitor.earthquakedata.network.EarthQuakeAPI
import com.earthquake.monitor.earthquakedata.view.fragment.EarthQuakeDetailsFragment
import com.earthquake.monitor.earthquakedata.view.fragment.EarthQuakeListViewFragment
import java.io.IOException
import java.lang.Exception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EarthQuakeListViewModel : ViewModel() {
    companion object {
        const val TAG: String = "MainViewModel"
    }

    // Fragment navigation
    private val replaceFragmentMutableLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val replaceFragmentLiveData: LiveData<String> = replaceFragmentMutableLiveData

    private val showProgressMutableLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val showProgressLiveData: LiveData<Boolean> = showProgressMutableLiveData

    private val featureDTOMutableLiveData: MutableLiveData<FeatureDTO> by lazy {
        MutableLiveData<FeatureDTO>()
    }
    val featureDTOLiveData: LiveData<FeatureDTO> = featureDTOMutableLiveData

    init {
        replaceFragmentMutableLiveData.value = EarthQuakeListViewFragment.TAG
        showProgressMutableLiveData.value = true
    }

    private val featureDTOListMutableLiveData: MutableLiveData<UIResource<List<FeatureDTO>?>> = MutableLiveData<UIResource<List<FeatureDTO>?>>()
    val featureDTOListLiveData: LiveData<UIResource<List<FeatureDTO>?>>
        get() = featureDTOListMutableLiveData

    fun getEarthQuakeList() {
        featureDTOListMutableLiveData.postValue(UIResource.loading(null))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Delay is intentionally added to show the progress bar on swipe to refresh and menu refresh
                delay(300)
                val response = EarthQuakeAPI.getEarthQuakeListAsync()
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let { featureCollectionDTO ->
                        if (featureCollectionDTO.features.isNotEmpty()) {
                            featureDTOListMutableLiveData.postValue(UIResource.success(featureCollectionDTO.features))
                        } else {
                            featureDTOListMutableLiveData.postValue(UIResource.error(response.message(), null, null, response.code()))
                        }
                    }
                } else {
                    featureDTOListMutableLiveData.postValue(UIResource.error(response.message(), null, null, response.code()))
                }
            } catch (e: HttpException) {
                Log.e(TAG, e.toString())
                featureDTOListMutableLiveData.postValue(UIResource.error("Error Occurred while fetching list: $e", e, null))
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
                featureDTOListMutableLiveData.postValue(UIResource.error("Network not available error message: $e", e, null))
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                featureDTOListMutableLiveData.postValue(UIResource.error("Error Occurred while fetching List: $e", e, null))
            }
        }
    }

    fun setShowProgress(show: Boolean) {
        showProgressMutableLiveData.postValue(show)
    }

    fun setSelectedFeatureDTO(featureDTO: FeatureDTO) {
        featureDTOMutableLiveData.postValue(featureDTO)
        replaceFragmentMutableLiveData.postValue(EarthQuakeDetailsFragment.TAG)
    }
}