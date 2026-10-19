package au.edu.jcu.assessment.utilityapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.data.api.NetworkClient
import au.edu.jcu.assessment.utilityapp.data.model.CityLocation
import au.edu.jcu.assessment.utilityapp.data.repository.AirQualityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UtilityViewModel(
    private val repository: AirQualityRepository = AirQualityRepository(NetworkClient.apiService)
) : ViewModel() {

    private val _uiState = MutableStateFlow<UtilityUiState>(UtilityUiState.Loading)
    val uiState: StateFlow<UtilityUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadDataForCity(city: CityLocation) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UtilityUiState.Loading
            val result = repository.fetchAirQuality(city.lat, city.lon)
            coroutineContext.ensureActive()
            result.onSuccess { response ->
                val timeFormat = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
                val formattedTime = timeFormat.format(Date())
                _uiState.value = UtilityUiState.Success(
                    data = response,
                    cityName = city.name,
                    lastUpdated = formattedTime
                )
            }.onFailure { throwable ->
                _uiState.value = UtilityUiState.Error(
                    message = throwable.localizedMessage ?: "Failed to fetch live air quality data. Please check network."
                )
            }
        }
    }
}