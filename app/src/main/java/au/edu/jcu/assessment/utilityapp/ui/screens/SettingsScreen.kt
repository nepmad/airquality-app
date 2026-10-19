package au.edu.jcu.assessment.utilityapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.data.model.AqiStandard
import au.edu.jcu.assessment.utilityapp.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by settingsViewModel.settings.collectAsState()
    var expandedCityDropdown by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        // The outer navigation Scaffold already supplies system-bar padding.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AirPulseBlue, titleContentColor = Color.White),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFF147CF0), Color(0xFFE5F1FF))))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("App Preferences", style = MaterialTheme.typography.titleMedium, color = Color.White)

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF172B48))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Default Location", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedCityDropdown,
                        onExpandedChange = { expandedCityDropdown = !expandedCityDropdown }
                    ) {
                        OutlinedTextField(
                            value = settings.selectedCity.name,
                            onValueChange = {},
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF172B48), unfocusedTextColor = Color(0xFF172B48)),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCityDropdown) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCityDropdown,
                            onDismissRequest = { expandedCityDropdown = false }
                        ) {
                            settingsViewModel.availableCities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city.name) },
                                    onClick = {
                                        settingsViewModel.updateCity(city)
                                        expandedCityDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF172B48))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AQI Standard Format", style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Use European AQI Standard", modifier = Modifier.weight(1f))
                        // This chooses which returned AQI field to display, not a unit conversion.
                        Switch(
                            checked = settings.aqiStandard == AqiStandard.EUROPEAN_CAQI,
                            onCheckedChange = { checked ->
                                settingsViewModel.updateAqiStandard(
                                    if (checked) AqiStandard.EUROPEAN_CAQI else AqiStandard.US_AQI
                                )
                            }
                        )
                    }
                }
            }


        }
    }
}
