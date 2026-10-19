package au.edu.jcu.assessment.utilityapp.ui.viewmodel

import au.edu.jcu.assessment.utilityapp.data.model.AirQualityResponse

// Mutually exclusive states keep loading, error and reading panels from appearing together.
sealed interface UtilityUiState {
    object Loading : UtilityUiState
    data class Success(
        val data: AirQualityResponse,
        val cityName: String,
        // Local fetch time for display, distinct from the API observation timestamp.
        val lastUpdated: String
    ) : UtilityUiState
    data class Error(val message: String) : UtilityUiState
}
