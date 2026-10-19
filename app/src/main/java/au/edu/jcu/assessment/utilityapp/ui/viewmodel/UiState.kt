package au.edu.jcu.assessment.utilityapp.ui.viewmodel

import au.edu.jcu.assessment.utilityapp.data.model.AirQualityResponse

sealed interface UtilityUiState {
    object Loading : UtilityUiState
    data class Success(
        val data: AirQualityResponse,
        val cityName: String,
        val lastUpdated: String
    ) : UtilityUiState
    data class Error(val message: String) : UtilityUiState
}
