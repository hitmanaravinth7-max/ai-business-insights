package com.example.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.BusinessProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun BusinessProfileDialog(
    profile: BusinessProfileEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Int, String, String) -> Unit
) {
    var companyName by remember { mutableStateOf(profile?.companyName ?: "") }
    var industry by remember { mutableStateOf(profile?.industry ?: "E-Commerce & Retail") }
    var budget by remember { mutableStateOf((profile?.monthlyBudget ?: 15000.0).toString()) }
    var teamSize by remember { mutableStateOf((profile?.teamSize ?: 10).toString()) }
    var primaryGoal by remember { mutableStateOf(profile?.primaryGoal ?: "Increase Profit Margin") }
    var targetAudience by remember { mutableStateOf(profile?.targetAudience ?: "") }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Navy900,
            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Business Profile Setup",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = White)
                )
                Text(
                    text = "Personalize AI recommendations and KPI targets",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = industry,
                    onValueChange = { industry = it },
                    label = { Text("Industry / Sector") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = { Text("Monthly Budget ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = teamSize,
                        onValueChange = { teamSize = it },
                        label = { Text("Team Size") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = primaryGoal,
                    onValueChange = { primaryGoal = it },
                    label = { Text("Primary Strategic Goal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetAudience,
                    onValueChange = { targetAudience = it },
                    label = { Text("Target Customers & Audience") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Slate400)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val bVal = budget.toDoubleOrNull() ?: 15000.0
                            val tVal = teamSize.toIntOrNull() ?: 5
                            onSave(companyName, industry, bVal, tVal, primaryGoal, targetAudience)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy950)
                    ) {
                        Text("Save Profile", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
