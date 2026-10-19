package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.data.api.AirQualityApiService
import au.edu.jcu.assessment.utilityapp.data.model.AirQualityResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repository layer handling network data fetching and error capturing
class AirQualityRepository(
    private val apiService: AirQualityApiService
) {
    // Fetch air quality data on IO thread pool dispatcher
    suspend fun fetchAirQuality(lat: Double, lon: Double): Result<AirQualityResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAirQuality(latitude = lat, longitude = lon)
                checkNotNull(response.current) { "No current air quality data is available." }
                Result.success(response)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Result.failure(error)
            }
        }
    }
}