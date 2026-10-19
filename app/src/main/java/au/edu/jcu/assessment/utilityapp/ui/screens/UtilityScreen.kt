package au.edu.jcu.assessment.utilityapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.assessment.utilityapp.data.model.AqiStandard
import au.edu.jcu.assessment.utilityapp.ui.viewmodel.SettingsViewModel
import au.edu.jcu.assessment.utilityapp.ui.viewmodel.UtilityUiState
import au.edu.jcu.assessment.utilityapp.ui.viewmodel.UtilityViewModel

val AirPulseBlue = Color(0xFF0646A5)
private val SkyGradient = Brush.verticalGradient(listOf(Color(0xFF147CF0), Color(0xFF80B7F5), Color(0xFFE5F1FF)))
private val Ink = Color(0xFF172B48)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SkyPage(title: String, modifier: Modifier, onRefresh: (() -> Unit)? = null,
                    content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AirPulseBlue, titleContentColor = Color.White),
                actions = {
                    if (onRefresh != null) IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, "Refresh readings", tint = Color.White)
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(SkyGradient)) {
            // Draw decorative clouds relative to the canvas size so they scale with the screen.
            Canvas(Modifier.fillMaxSize()) {
                val cloud = Color.White.copy(alpha = 0.19f)
                listOf(Offset(size.width * .12f, size.height * .13f),
                    Offset(size.width * .85f, size.height * .29f),
                    Offset(size.width * .18f, size.height * .67f)).forEach { origin ->
                    val radius = size.width * .035f
                    drawCircle(cloud, radius, origin)
                    drawCircle(cloud, radius * 1.4f, origin + Offset(radius * 1.4f, -radius * .5f))
                    drawCircle(cloud, radius, origin + Offset(radius * 2.8f, 0f))
                }
            }
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                content = content
            )
        }
    }
}

@Composable
fun UtilityScreen(viewModel: UtilityViewModel, settingsViewModel: SettingsViewModel,
                  modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    // Fetch on entry and city changes; ordinary recomposition does not restart this effect.
    LaunchedEffect(settings.selectedCity) {
        viewModel.loadDataForCity(settings.selectedCity)
    }
    var showCityPicker by remember { mutableStateOf(false) }
    val european = settings.aqiStandard == AqiStandard.EUROPEAN_CAQI
    val scale = if (european) "European AQI" else "US AQI"
    SkyPage("AirPulse", modifier,
        onRefresh = { viewModel.loadDataForCity(settings.selectedCity) }) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                TextButton(onClick = { showCityPicker = true }) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.White)
                    Text(settings.selectedCity.name, color = Color.White,
                        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowDropDown, "Change location", tint = Color.White)
                }
                Text("Current atmospheric conditions", color = Color.White,
                    style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.Default.Air, null, tint = Color.White, modifier = Modifier.size(48.dp))
        }
        when (val currentState = state) {
            UtilityUiState.Loading -> {
                Column(Modifier.fillMaxWidth().padding(vertical = 64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(Modifier.height(16.dp))
                    Text("Fetching air quality…", color = Color.White)
                }
            }
            is UtilityUiState.Error -> WhitePanel {
                Text("Readings unavailable", style = MaterialTheme.typography.titleLarge, color = Ink)
                Text(currentState.message, color = Ink)
                Button(onClick = { viewModel.loadDataForCity(settings.selectedCity) }) { Text("Try again") }
            }
            is UtilityUiState.Success -> {
                val reading = currentState.data.current
                // Treat missing or negative indices as unavailable rather than as good air quality.
                val aqi = (if (european) reading?.europeanAqi else reading?.usAqi)?.takeIf { it >= 0 }
                val (category, color) = if (european) getEuropeanAqiCategory(aqi) else getAqiCategory(aqi)
                run {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(
                            Modifier.size(200.dp).border(5.dp, Color.White, CircleShape)
                                .background(Color.White.copy(alpha = .08f), CircleShape).padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(aqi?.toString() ?: "—", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Bold)
                            Text(scale, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(if (aqi == null) "Unavailable" else category, color = Color.White,
                                style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
                        }
                    }
                }
                Text("Fetched: ${currentState.lastUpdated}", color = Color.White,
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium)
                WhitePanel {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Pollutants", color = Ink,
                            style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f))
                        Icon(Icons.Default.Air, null, tint = AirPulseBlue)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ParameterTile("PM2.5", reading?.pm25, "µg/m³", Icons.Default.Air, Modifier.weight(1f))
                        ParameterTile("PM10", reading?.pm10, "µg/m³", Icons.Default.Air, Modifier.weight(1f))
                    }
                    HorizontalDivider(color = Color(0xFFE1EBF5))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ParameterTile("Dust", reading?.dust, "µg/m³", Icons.Default.Air, Modifier.weight(1f))
                        ParameterTile("UV index", reading?.uvIndex, "", Icons.Default.WbSunny, Modifier.weight(1f))
                    }
                }
                WhitePanel {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(12.dp).background(color, CircleShape))
                        Text(category, color = Ink, fontWeight = FontWeight.Bold)
                    }
                    Text(if (european) getEuropeanAqiHealthAdvice(aqi) else getAqiHealthAdvice(aqi), color = Ink,
                        style = MaterialTheme.typography.bodyMedium)
                }
                Text("Model-derived conditions · Open-Meteo / CAMS", color = Ink,
                    style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }
        }
    }
    if (showCityPicker) {
        AlertDialog(
            onDismissRequest = { showCityPicker = false },
            title = { Text("Choose location") },
            text = {
                Column {
                    settingsViewModel.availableCities.forEach { city ->
                        TextButton(
                            onClick = {
                                // The shared settings state triggers a fresh request for this city.
                                settingsViewModel.updateCity(city)
                                showCityPicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(city.name, modifier = Modifier.weight(1f))
                            if (city == settings.selectedCity) {
                                Icon(Icons.Default.Check, "Selected location")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityPicker = false }) { Text("Cancel") }
            }
        )
    }

}

@Composable
private fun WhitePanel(content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}

@Composable
private fun ParameterTile(title: String, value: Double?, unit: String, icon: ImageVector, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, null, tint = AirPulseBlue, modifier = Modifier.size(28.dp))
        // Format available readings for the device locale; keep missing values visibly unavailable.
        Text(value?.let { String.format(java.util.Locale.getDefault(), "%.1f", it) } ?: "Unavailable",
            style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Ink)
        Text(if (unit.isEmpty()) title else "$title · $unit", color = Ink,
            style = MaterialTheme.typography.labelMedium)
        Box(Modifier.fillMaxWidth().height(3.dp).background(Color(0xFF4CA5F5), CircleShape))
    }
}

// US and European AQI use different bands; select the helper matching the displayed scale.
fun getAqiCategory(aqi: Int?): Pair<String, Color> {
    return when {
        aqi == null || aqi < 0 -> "Unavailable" to Color.Gray
        aqi <= 50 -> "Good" to Color(0xFF4CAF50)
        aqi <= 100 -> "Moderate" to Color(0xFFFFC107)
        aqi <= 150 -> "Unhealthy for Sensitive Groups" to Color(0xFFFF9800)
        aqi <= 200 -> "Unhealthy" to Color(0xFFF44336)
        aqi <= 300 -> "Very Unhealthy" to Color(0xFF9C27B0)
        else -> "Hazardous" to Color(0xFF7E0023)
    }
}

// Helper mapping AQI level to practical health recommendations
fun getAqiHealthAdvice(aqi: Int?): String {
    return when {
        aqi == null || aqi < 0 -> "Air quality data is unavailable. Check a local air quality service."
        aqi <= 50 -> "Air quality is satisfactory. Enjoy outdoor activities!"
        aqi <= 100 -> "Air quality is acceptable. Sensitive individuals should consider reducing prolonged outdoor exertion."
        aqi <= 150 -> "Sensitive groups may experience health effects. General public is less likely to be affected."
        else -> "Health alert! Everyone may experience more serious health effects. Limit outdoor activity."
    }
}

// Open-Meteo's European AQI is distinct from the CAQI index.
fun getEuropeanAqiCategory(aqi: Int?): Pair<String, Color> = when {
    aqi == null || aqi < 0 -> "Unavailable" to Color.Gray
    aqi <= 20 -> "Good" to Color(0xFF388E3C)
    aqi <= 40 -> "Fair" to Color(0xFF558B2F)
    aqi <= 60 -> "Moderate" to Color(0xFFF9A825)
    aqi <= 80 -> "Poor" to Color(0xFFEF6C00)
    aqi <= 100 -> "Very Poor" to Color(0xFFC62828)
    else -> "Extremely Poor" to Color(0xFF6A1B9A)
}

fun getEuropeanAqiHealthAdvice(aqi: Int?): String = when {
    aqi == null || aqi < 0 -> "Air quality data is unavailable. Check a local air quality service."
    aqi <= 40 -> "Air quality is suitable for usual outdoor activities."
    aqi <= 60 -> "Sensitive individuals should consider reducing outdoor exertion if they experience symptoms."
    else -> "Reduce outdoor exertion, especially if you are sensitive to air pollution. Check local guidance."
}
