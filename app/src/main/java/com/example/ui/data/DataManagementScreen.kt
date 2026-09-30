package com.example.ui.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.MonthlyMetricEntity
import com.example.domain.analytics.format
import com.example.domain.analytics.formatCurrency
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun DataManagementScreen(viewModel: MainViewModel) {
    val metrics by viewModel.monthlyMetrics.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showCsvDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .statusBarsPadding()
            .testTag("data_management_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp)
    ) {
        // Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Business Data Management",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    )
                    Text(
                        text = "Input records, import CSV data, or load industry benchmarks",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Industry Benchmark Presets (Load Demo Data)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Navy700, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Cyan400, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Switch Industry Demo Dataset",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.loadDemoDataset("Retail") },
                            modifier = Modifier.weight(1f).testTag("load_retail_dataset_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate200)
                        ) {
                            Text("Retail", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        OutlinedButton(
                            onClick = { viewModel.loadDemoDataset("SaaS") },
                            modifier = Modifier.weight(1f).testTag("load_saas_dataset_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate200)
                        ) {
                            Text("SaaS / Tech", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        OutlinedButton(
                            onClick = { viewModel.loadDemoDataset("Bakery") },
                            modifier = Modifier.weight(1f).testTag("load_bakery_dataset_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate200)
                        ) {
                            Text("Bakery / Food", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Buttons Row (Add Record & CSV Import)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f).testTag("add_monthly_record_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy950)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Month Data", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = { showCsvDialog = true },
                    modifier = Modifier.weight(1f).testTag("import_csv_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate200)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Paste CSV", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Monthly Financial Records Table
        item {
            Text(
                text = "Recorded Financial History (${metrics.size} Months)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = White)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(metrics.reversed()) { m ->
            val totalExpenses = m.cogs + m.operatingExpenses + m.marketingSpend
            val netProfit = m.revenue - totalExpenses

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, Navy700, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${m.monthName} ${m.year}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = White)
                        )

                        Text(
                            text = "Net: ${netProfit.formatCurrency()}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (netProfit >= 0) Emerald500 else Rose500
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Revenue: ${m.revenue.formatCurrency()}", style = MaterialTheme.typography.bodySmall.copy(color = Slate300))
                        Text("Expenses: ${totalExpenses.formatCurrency()}", style = MaterialTheme.typography.bodySmall.copy(color = Slate400))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Marketing: ${m.marketingSpend.formatCurrency()}", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                        Text("New Cust: ${m.newCustomers} • Churn: ${(m.churnRate * 100).format(1)}%", style = MaterialTheme.typography.labelSmall.copy(color = Cyan400))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMetricDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { month, rev, cogs, opex, mkt, newC, churn ->
                viewModel.addManualMetric(month, rev, cogs, opex, mkt, newC, churn)
                showAddDialog = false
            }
        )
    }

    if (showCsvDialog) {
        CsvImportDialog(
            onDismiss = { showCsvDialog = false },
            onImportRows = { rows ->
                rows.forEach { (m, rev, cogs, opex, mkt, newC, churn) ->
                    viewModel.addManualMetric(m, rev, cogs, opex, mkt, newC, churn)
                }
                showCsvDialog = false
            }
        )
    }
}

@Composable
private fun AddMetricDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Double, Double, Double, Double, Int, Double) -> Unit
) {
    var monthName by remember { mutableStateOf("Sep") }
    var revenue by remember { mutableStateOf("88000") }
    var cogs by remember { mutableStateOf("41000") }
    var opex by remember { mutableStateOf("18000") }
    var marketing by remember { mutableStateOf("9500") }
    var newCust by remember { mutableStateOf("580") }
    var churnPercent by remember { mutableStateOf("3.2") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Navy900,
            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add Monthly Financials",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = White)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = monthName,
                    onValueChange = { monthName = it },
                    label = { Text("Month Name (e.g. Sep)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = revenue,
                        onValueChange = { revenue = it },
                        label = { Text("Revenue ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cogs,
                        onValueChange = { cogs = it },
                        label = { Text("COGS ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = opex,
                        onValueChange = { opex = it },
                        label = { Text("OPEX ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = marketing,
                        onValueChange = { marketing = it },
                        label = { Text("Marketing ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newCust,
                        onValueChange = { newCust = it },
                        label = { Text("New Customers") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = churnPercent,
                        onValueChange = { churnPercent = it },
                        label = { Text("Churn %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                            val revVal = revenue.toDoubleOrNull() ?: 0.0
                            val cogsVal = cogs.toDoubleOrNull() ?: 0.0
                            val opexVal = opex.toDoubleOrNull() ?: 0.0
                            val mktVal = marketing.toDoubleOrNull() ?: 0.0
                            val custVal = newCust.toIntOrNull() ?: 100
                            val churnVal = (churnPercent.toDoubleOrNull() ?: 3.0) / 100.0
                            onAdd(monthName, revVal, cogsVal, opexVal, mktVal, custVal, churnVal)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy950)
                    ) {
                        Text("Save Record", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CsvImportDialog(
    onDismiss: () -> Unit,
    onImportRows: (List<MetricCsvRow>) -> Unit
) {
    var csvText by remember {
        mutableStateOf(
            "Sep, 88500, 41200, 18200, 9600, 590, 0.033\n" +
            "Oct, 92400, 42900, 18500, 9900, 620, 0.031"
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Navy900,
            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Quick CSV Importer",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = White)
                )

                Text(
                    text = "Format: Month, Revenue, COGS, OPEX, Marketing, NewCust, ChurnRate",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = csvText,
                    onValueChange = { csvText = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = Slate200,
                        focusedContainerColor = Navy800,
                        unfocusedContainerColor = Navy800
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                            val rows = mutableListOf<MetricCsvRow>()
                            csvText.lines().forEach { line ->
                                val parts = line.split(",").map { it.trim() }
                                if (parts.size >= 5) {
                                    val m = parts[0]
                                    val rev = parts[1].toDoubleOrNull() ?: 0.0
                                    val cogs = parts[2].toDoubleOrNull() ?: 0.0
                                    val opex = parts[3].toDoubleOrNull() ?: 0.0
                                    val mkt = parts[4].toDoubleOrNull() ?: 0.0
                                    val cust = parts.getOrNull(5)?.toIntOrNull() ?: 100
                                    val churn = parts.getOrNull(6)?.toDoubleOrNull() ?: 0.03
                                    rows.add(MetricCsvRow(m, rev, cogs, opex, mkt, cust, churn))
                                }
                            }
                            onImportRows(rows)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy950)
                    ) {
                        Text("Import CSV Rows", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class MetricCsvRow(
    val monthName: String,
    val revenue: Double,
    val cogs: Double,
    val opex: Double,
    val marketing: Double,
    val newCustomers: Int,
    val churn: Double
)
