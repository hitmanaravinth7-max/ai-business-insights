package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.*
import com.example.ui.auth.AuthScreen
import com.example.ui.chat.AiConsultantChatScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.data.DataManagementScreen
import com.example.ui.dialogs.BusinessProfileDialog
import com.example.ui.dialogs.ExecutiveReportDialog
import com.example.ui.predictive.PredictiveInsightsScreen
import com.example.ui.recommendations.RecommendationsScreen
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    val authState by viewModel.authUiState.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val showProfileDialog by viewModel.showProfileDialog.collectAsState()
    val showReportDialog by viewModel.showReportDialog.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val metrics by viewModel.monthlyMetrics.collectAsState()
    val channels by viewModel.marketingChannels.collectAsState()
    val products by viewModel.products.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()

    if (!authState.isLoggedIn) {
        AuthScreen(viewModel = viewModel)
    } else {
        // Handle hardware / gesture back button to return to Dashboard
        if (currentScreen != ScreenDestination.Dashboard) {
            BackHandler {
                viewModel.navigateTo(ScreenDestination.Dashboard)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Navy950,
            bottomBar = {
                AppBottomNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        ScreenDestination.Dashboard -> DashboardScreen(viewModel = viewModel)
                        ScreenDestination.Predictive -> PredictiveInsightsScreen(viewModel = viewModel)
                        ScreenDestination.Recommendations -> RecommendationsScreen(viewModel = viewModel)
                        ScreenDestination.AiChat -> AiConsultantChatScreen(viewModel = viewModel)
                        ScreenDestination.DataManager -> DataManagementScreen(viewModel = viewModel)
                    }
                }
            }
        }

        // Business Profile Setup / Edit Dialog
        if (showProfileDialog) {
            BusinessProfileDialog(
                profile = profile,
                onDismiss = { viewModel.closeProfileDialog() },
                onSave = { name, ind, bud, team, goal, aud ->
                    viewModel.saveBusinessProfile(name, ind, bud, team, goal, aud)
                }
            )
        }

        // Executive Report Dialog
        if (showReportDialog) {
            ExecutiveReportDialog(
                profile = profile,
                metrics = metrics,
                channels = channels,
                products = products,
                recommendations = recommendations,
                onDismiss = { viewModel.closeReportDialog() }
            )
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("main_bottom_nav"),
        containerColor = Navy900,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(ScreenDestination.Dashboard, Icons.Default.Dashboard, Icons.Outlined.Dashboard),
            Triple(ScreenDestination.Predictive, Icons.Default.AutoGraph, Icons.Outlined.AutoGraph),
            Triple(ScreenDestination.Recommendations, Icons.Default.Lightbulb, Icons.Outlined.Lightbulb),
            Triple(ScreenDestination.AiChat, Icons.Default.SmartToy, Icons.Outlined.SmartToy),
            Triple(ScreenDestination.DataManager, Icons.Default.TableChart, Icons.Outlined.TableChart)
        )

        navItems.forEach { (destination, selectedIcon, unselectedIcon) ->
            val isSelected = currentScreen == destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) selectedIcon else unselectedIcon,
                        contentDescription = destination.title,
                        tint = if (isSelected) Navy950 else Slate400
                    )
                },
                label = {
                    Text(
                        text = destination.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Cyan400 else Slate400
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Cyan400,
                    selectedIconColor = Navy950,
                    unselectedIconColor = Slate400
                ),
                modifier = Modifier.testTag("nav_item_${destination.route}")
            )
        }
    }
}
