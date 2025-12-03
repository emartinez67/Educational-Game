package com.example.project3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun ChildProgressScreen(
    navController: NavController,
    childEmail: String,
    childProgressViewModel: ChildProgressViewModel = viewModel(
        factory = ChildProgressViewModel.Factory
    ),
    onUpClick: () -> Unit = { }
) {
    LaunchedEffect(childEmail) {
        childProgressViewModel.loadChildData(childEmail)
        childProgressViewModel.loadProgressData(childEmail)
    }

    Scaffold(
        topBar = {
            EducationalGameAppBar(
                canNavigateBack = true,
                onUpClick = onUpClick,
                title = "Progress Report"
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "${childProgressViewModel.childFirstName} ${childProgressViewModel.childLastName}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = childProgressViewModel.childEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Member since: ${childProgressViewModel.accountCreated}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Games\nCompleted",
                        value = "${childProgressViewModel.totalGamesCompleted}",
                        color = Color(0xFF4CAF50)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total\nScore",
                        value = "${childProgressViewModel.totalScore}",
                        color = Color(0xFFFF9800)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Avg\nScore",
                        value = String.format("%.0f", childProgressViewModel.averageScore),
                        color = Color(0xFF2196F3)
                    )
                }
            }
            if (childProgressViewModel.progressData.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "📈 Score Trends",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Performance over time",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            AndroidView(
                                factory = { context ->
                                    LineChart(context).apply {
                                        description.isEnabled = false
                                        setTouchEnabled(true)
                                        setPinchZoom(true)
                                        setDrawGridBackground(false)

                                        xAxis.apply {
                                            position = XAxis.XAxisPosition.BOTTOM
                                            setDrawGridLines(false)
                                            granularity = 1f
                                            textSize = 10f
                                        }

                                        axisLeft.apply {
                                            setDrawGridLines(true)
                                            axisMinimum = 0f
                                            axisMaximum = 110f
                                            textSize = 10f
                                        }

                                        axisRight.isEnabled = false

                                        legend.apply {
                                            verticalAlignment = Legend.LegendVerticalAlignment.TOP
                                            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                                            orientation = Legend.LegendOrientation.HORIZONTAL
                                            setDrawInside(false)
                                            textSize = 10f
                                        }
                                    }
                                },
                                update = { chart ->
                                    val entries = childProgressViewModel.progressData.mapIndexed { index, progress ->
                                        Entry(index.toFloat(), progress.score.toFloat())
                                    }

                                    val dataSet = LineDataSet(entries, "Score").apply {
                                        color = android.graphics.Color.rgb(33, 150, 243)
                                        setCircleColor(android.graphics.Color.rgb(33, 150, 243))
                                        lineWidth = 2f
                                        circleRadius = 4f
                                        setDrawCircleHole(false)
                                        valueTextSize = 10f
                                        setDrawFilled(true)
                                        fillColor = android.graphics.Color.rgb(33, 150, 243)
                                        fillAlpha = 50
                                        mode = LineDataSet.Mode.CUBIC_BEZIER
                                    }

                                    chart.data = LineData(dataSet)
                                    chart.invalidate()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}