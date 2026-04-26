package com.smartphonedoctor.presentation.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartphonedoctor.domain.model.HealthScore
import com.smartphonedoctor.presentation.ui.theme.ColorAmber
import com.smartphonedoctor.presentation.ui.theme.ColorGreen
import com.smartphonedoctor.presentation.ui.theme.ColorRed

@Composable
fun HealthScoreScreen(
    onSeeIssuesClick: () -> Unit,
    healthScore: HealthScore = HealthScore(
        overallScore = 82,
        batteryScore = 75,
        storageScore = 85,
        perfScore = 88
    )
) {
    var animationPlayed by remember { mutableStateOf(false) }
    
    val animatedScore by animateFloatAsState(
        targetValue = if (animationPlayed) healthScore.overallScore.toFloat() else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "score_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Your Phone Health",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Circular Gauge
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            val gaugeColor = getScoreColor(animatedScore.toInt())
            val trackColor = MaterialTheme.colorScheme.surfaceVariant
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = trackColor,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )
                
                drawArc(
                    color = gaugeColor,
                    startAngle = 135f,
                    sweepAngle = 270f * (animatedScore / 100f),
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${animatedScore.toInt()}",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(text = "out of 100", style = MaterialTheme.typography.bodyMedium)
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Sub-scores
        ScoreRow(title = "Battery", score = healthScore.batteryScore)
        Spacer(modifier = Modifier.height(16.dp))
        ScoreRow(title = "Storage", score = healthScore.storageScore)
        Spacer(modifier = Modifier.height(16.dp))
        ScoreRow(title = "Performance", score = healthScore.perfScore)
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onSeeIssuesClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("See Issues", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ScoreRow(title: String, score: Int) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedScore by animateFloatAsState(
        targetValue = if (animationPlayed) score.toFloat() else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "${title}_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(text = "$score / 100", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = animatedScore / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = getScoreColor(score),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

fun getScoreColor(score: Int): Color {
    return when {
        score >= 70 -> ColorGreen
        score >= 40 -> ColorAmber
        else -> ColorRed
    }
}
