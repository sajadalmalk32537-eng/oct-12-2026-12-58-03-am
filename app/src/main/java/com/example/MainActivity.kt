package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ScreenRoute
import com.example.ui.components.TamaneenaBottomBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoyalDarkNavy
import com.example.viewmodel.TamaneenaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    TamaneenaApp()
                }
            }
        }
    }
}

@Composable
fun TamaneenaApp(viewModel: TamaneenaViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val context = LocalContext.current

    // Notification Permission for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Back handling
    BackHandler(enabled = currentScreen != ScreenRoute.HOME) {
        val handled = viewModel.navigateBack()
        if (!handled) {
            viewModel.navigateTo(ScreenRoute.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = RoyalDarkNavy,
        bottomBar = {
            TamaneenaBottomBar(
                currentScreen = currentScreen,
                onNavigate = { route -> viewModel.navigateTo(route) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenRoute.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.QURAN -> QuranScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.SURAH_READER -> SurahReaderScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ScreenRoute.DUAS -> DuasScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.DUA_READER -> DuaReaderScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ScreenRoute.MUNAJAT -> MunajatScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.MUNAJAT_READER -> MunajatScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.ZIYARAT -> ZiyaratScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.ZIYARAT_READER -> ZiyaratScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.AZKAR -> AzkarScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.TASBIH -> TasbihScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.SALAT_LAYL -> SalatLaylScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ScreenRoute.AHLULBAYT -> AhlulBaytScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ScreenRoute.FIQH_CHAT -> FiqhChatScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ScreenRoute.SEARCH -> SearchScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
                ScreenRoute.SETTINGS -> AlarmsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                ScreenRoute.FAVORITES -> SearchScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
            }
        }
    }
}
