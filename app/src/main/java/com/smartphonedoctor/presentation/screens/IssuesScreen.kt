package com.smartphonedoctor.presentation.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartphonedoctor.domain.model.Issue
import com.smartphonedoctor.domain.model.Severity
import com.smartphonedoctor.presentation.ui.theme.ColorAmber
import com.smartphonedoctor.presentation.ui.theme.ColorGreen
import com.smartphonedoctor.presentation.ui.theme.ColorRed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle

@Composable
fun IssuesScreen(
    issues: List<Issue> = emptyList()
) {
    if (issues.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Healthy",
                    modifier = Modifier.size(64.dp),
                    tint = ColorGreen
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your phone looks healthy!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No critical issues found during the last scan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }
    val groupedIssues = issues.groupBy { it.severity }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Detected Issues",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Show Critical first, then Warning, then Info
        listOf(Severity.CRITICAL, Severity.WARNING, Severity.INFO).forEach { severity ->
            val severityIssues = groupedIssues[severity]
            if (!severityIssues.isNullOrEmpty()) {
                item {
                    Text(
                        text = severity.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = getSeverityColor(severity)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(severityIssues) { issue ->
                    IssueCard(issue)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun IssueCard(issue: Issue) {
    var showManualFix by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = getSeverityColor(issue.severity).copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = issue.severity.name,
                        color = getSeverityColor(issue.severity),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = issue.description, style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (issue.deepLinkAction != null) {
                Button(onClick = {
                    try {
                        context.startActivity(Intent(issue.deepLinkAction))
                    } catch (e: ActivityNotFoundException) {
                        showManualFix = true
                    }
                }) {
                    Text("Fix Now")
                }
            } else {
                OutlinedButton(onClick = { showManualFix = !showManualFix }) {
                    Text("Manual Fix")
                }
            }

            AnimatedVisibility(visible = showManualFix || (issue.deepLinkAction == null && showManualFix)) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("Instructions:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    Text(text = issue.exactFix, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

fun getSeverityColor(severity: Severity) = when (severity) {
    Severity.CRITICAL -> ColorRed
    Severity.WARNING -> ColorAmber
    Severity.INFO -> ColorGreen
}
