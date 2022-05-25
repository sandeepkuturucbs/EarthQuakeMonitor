package com.earthquake.monitor.earthquakedata.network

import androidx.annotation.WorkerThread
import com.earthquake.monitor.base.network.RetrofitApiProvider
import com.earthquake.monitor.earthquakedata.model.FeatureCollectionDTO
import com.earthquake.monitor.earthquakedata.util.Constants
import retrofit2.Response
import retrofit2.http.GET

class EarthQuakeAPI {
    private interface Api {
        @GET("all_hour.geojson")
        suspend fun getEarthQuakeListAsync(): Response<FeatureCollectionDTO?>
    }

    companion object {
        @WorkerThread
        suspend fun getEarthQuakeListAsync(): Response<FeatureCollectionDTO?> {
            return getService().getEarthQuakeListAsync()
        }

        private fun getService(): Api {
            return RetrofitApiProvider.provideApiService(Constants.EARTHQUAKE_BASE_URL, Api::class.java)
        }
    }
}