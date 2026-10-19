package au.edu.jcu.assessment.utilityapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import au.edu.jcu.assessment.utilityapp.data.model.AqiStandard
import au.edu.jcu.assessment.utilityapp.data.model.CityLocation
import au.edu.jcu.assessment.utilityapp.data.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {
    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    val availableCities = listOf(
        CityLocation("Kathmandu", 27.7172, 85.3240),
        CityLocation("Brisbane", -27.4705, 153.0260),
        CityLocation("Sydney", -33.8688, 151.2093),
        CityLocation("London", 51.5074, -0.1278),
        CityLocation("Tokyo", 35.6762, 139.6503)
    )

    fun updateCity(city: CityLocation) {
        _settings.update { it.copy(selectedCity = city) }
    }

    fun updateAqiStandard(standard: AqiStandard) {
        _settings.update { it.copy(aqiStandard = standard) }
    }

    fun toggleAutoRefresh(enabled: Boolean) {
        _settings.update { it.copy(autoRefresh = enabled) }
    }
}