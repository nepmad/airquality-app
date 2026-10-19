package au.edu.jcu.assessment.utilityapp.data.api

import au.edu.jcu.assessment.utilityapp.data.model.AirQualityResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Request both AQI scales together so switching the display does not require another call.
interface AirQualityApiService {
    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") currentMetrics: String = "us_aqi,european_aqi,pm2_5,pm10,dust,uv_index"
    ): AirQualityResponse
}