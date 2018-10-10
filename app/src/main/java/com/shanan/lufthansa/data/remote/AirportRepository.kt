package com.shanan.lufthansa.data.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.shanan.lufthansa.api.LufthansaService
import com.shanan.lufthansa.api.getAirports
import com.shanan.lufthansa.data.db.AirportLocalCache
import com.shanan.lufthansa.model.AirportSearchResult

/**
 * Repository class that works with local and remote data sources.
 */
class AirportRepository(
        private val service: LufthansaService,
        private val cache: AirportLocalCache
) {


    // keep the last requested page. When the request is successful, increment the page number.
    private var offset = 0

    // LiveData of network errors.
    private val networkErrors = MutableLiveData<String>()

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    /**
     * Search airports whose names match the query.
     */
    fun search(query: String): AirportSearchResult {
        Log.d("AirportRepository", "New query: $query")
        offset = 0
        requestAndSaveData(query)

        // Get data from the local cache
        val data = cache.airportByName(query)

        return AirportSearchResult(data, networkErrors)
    }

    fun requestMore(query: String) {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        getAirports(service, query, offset, LIMIT, { airports ->
            cache.insert(airports, {
                offset += LIMIT
                isRequestInProgress = false
            })
        }, { error ->
            networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }

    companion object {
        private const val LIMIT = 100
    }
}