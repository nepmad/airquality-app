package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import au.edu.jcu.assessment.utilityapp.ui.UtilityApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Save the launch state so rotating the device does not replay the splash.
                    var showSplash by rememberSaveable { mutableStateOf(true) }
                    LaunchedEffect(Unit) {
                        delay(1000)
                        showSplash = false
                    }
                    if (showSplash) {
                        Column(
                            modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color(0xFF0646A5), Color(0xFF147CF0)))
                            ).safeDrawingPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_launcher_foreground),
                                contentDescription = "AirPulse logo",
                                modifier = Modifier.size(112.dp)
                            )
                            Spacer(Modifier.height(20.dp))
                            Text("AirPulse", color = Color.White, fontSize = 32.sp,
                                fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text("Air quality at a glance", color = Color.White)
                        }
                    } else {
                        UtilityApp()
                    }
                }
            }
        }
    }
}