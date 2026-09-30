package com.example.ui.recommendations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BusinessRecommendationEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun RecommendationsScreen(viewModel: MainViewModel) {
    val recommendations by viewModel.recommendations.collectAsState()
    val isGenerating by viewModel.isGeneratingAiRecs.collectAsState()

    var selectedPriorityFilter by remember { mutableStateOf("All") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val filteredList = remember(recommendations, selectedPriorityFilter, selectedCategoryFilter) {
        recommendations.filter { rec ->
            val priorityMatches = selectedPriorityFilter == "All" || rec.priority.equals(selectedPriorityFilter, ignoreCase = true)
            val categoryMatches = selectedCategoryFilter == "All" || rec.category.contains(selectedCategoryFilter, ignoreCase = true)
            priorityMatches && categoryMatches
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .statusBarsPadding()
            .testTag("recommendations_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp)
    ) {
        // Title & AI Generator Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Recommendation Engine",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    )
                    Text(
                        text = "Prioritized actions to improve profitability & reduce risk",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                }

                Button(
                    onClick = { viewModel.triggerAiRecommendations() },
                    enabled = !isGenerating,
                    modifier = Modifier.testTag("generate_ai_recommendations_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy950)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Navy950, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analyzing...", style = MaterialTheme.typography.labelSmall)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ask Gemini", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Priority Filter Chips
        item {
            val priorities = listOf("All", "High", "Medium", "Low")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(priorities) { prio ->
                    val isSel = selectedPriorityFilter == prio
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedPriorityFilter = prio },
                        label = { Text(prio) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Cyan500,
                            selectedLabelColor = Navy950,
                            containerColor = Navy900,
                            labelColor = Slate300
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSel) Cyan500 else Navy700,
                            enabled = true,
                            selected = isSel
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // List of Recommendations
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp)
                        .border(1.dp, Navy700, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy900)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Slate500, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No recommendations match this filter", style = MaterialTheme.typography.bodyMedium.copy(color = Slate300))
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { rec ->
                RecommendationCard(
                    recommendation = rec,
                    onStatusChange = { newStatus -> viewModel.updateRecommendationStatus(rec.id, newStatus) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: BusinessRecommendationEntity,
    onStatusChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val priorityColor = when (recommendation.priority.lowercase()) {
        "high" -> Rose500
        "medium" -> Amber500
        else -> Cyan400
    }

    val statusColor = when (recommendation.status.lowercase()) {
        "completed" -> Emerald500
        "in progress" -> Cyan400
        else -> Slate400
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Navy700, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Navy900)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Priority Badge
                    Surface(
                        color = priorityColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${recommendation.priority} Priority",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = priorityColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Category Badge
                    Surface(
                        color = Navy800,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                    ) {
                        Text(
                            text = recommendation.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate300,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Status Badge Clickable Dropdown / Switcher
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable {
                        val nextStatus = when (recommendation.status) {
                            "Pending" -> "In Progress"
                            "In Progress" -> "Completed"
                            else -> "Pending"
                        }
                        onStatusChange(nextStatus)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(statusColor))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = recommendation.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Impact Callout
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Emerald500, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Expected Impact: ${recommendation.expectedImpact}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Emerald500,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Summary
            Text(
                text = recommendation.summary,
                style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Action Steps
            AnimatedVisibility(visible = expanded) {
                Surface(
                    color = Navy800,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Implementation Action Plan:",
                            style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = recommendation.actionSteps,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate200, lineHeight = 20.sp)
                        )
                    }
                }
            }

            // View steps toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Hide action plan" else "View action plan",
                    style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontWeight = FontWeight.Bold)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Cyan400,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
