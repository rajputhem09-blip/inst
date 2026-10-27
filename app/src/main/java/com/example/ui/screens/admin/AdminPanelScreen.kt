package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ModerationAction
import com.example.data.model.ReportStatus
import com.example.data.model.VerificationStatus
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.LykoCyanTertiary
import com.example.ui.theme.LykoMagentaSecondary
import com.example.ui.theme.LykoVioletPrimary
import com.example.ui.viewmodel.LykoViewModel
import com.example.ui.viewmodel.NavigationDestination

enum class AdminTab {
    REPORTS,
    VERIFICATION,
    USERS,
    ADS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: LykoViewModel,
    modifier: Modifier = Modifier
) {
    val reports by viewModel.reports.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val adCampaigns by viewModel.adCampaigns.collectAsState()

    var activeAdminTab by remember { mutableStateOf(AdminTab.REPORTS) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = LykoVioletPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin & Moderation Hub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(NavigationDestination.FEED) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Metrics Summary Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    title = "Pending Reports",
                    value = "${reports.count { it.statusEnum == ReportStatus.PENDING }}",
                    color = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Active Users",
                    value = "${allUsers.size}",
                    color = LykoCyanTertiary,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Ad Budget",
                    value = "$2.8K",
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = activeAdminTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = LykoVioletPrimary,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = activeAdminTab == AdminTab.REPORTS,
                    onClick = { activeAdminTab = AdminTab.REPORTS },
                    text = { Text("Reports (${reports.count { it.statusEnum == ReportStatus.PENDING }})") }
                )
                Tab(
                    selected = activeAdminTab == AdminTab.VERIFICATION,
                    onClick = { activeAdminTab = AdminTab.VERIFICATION },
                    text = { Text("Verification") }
                )
                Tab(
                    selected = activeAdminTab == AdminTab.USERS,
                    onClick = { activeAdminTab = AdminTab.USERS },
                    text = { Text("Users") }
                )
                Tab(
                    selected = activeAdminTab == AdminTab.ADS,
                    onClick = { activeAdminTab = AdminTab.ADS },
                    text = { Text("Ads") }
                )
            }

            // Tab Body Content
            when (activeAdminTab) {
                AdminTab.REPORTS -> {
                    val pendingReports = reports.filter { it.statusEnum == ReportStatus.PENDING }
                    if (pendingReports.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "All clear! No pending reports.",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pendingReports) { report ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_report_${report.id}")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFEF4444).copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "Target: ${report.targetType.uppercase()}",
                                                    color = Color(0xFFEF4444),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Text(
                                                text = report.formattedTime,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Reason: ${report.reason}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )

                                        Text(
                                            text = "Reported by @${report.reporterUser.username} against Target ID #${report.targetId}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    viewModel.resolveReport(report.id, ModerationAction.DELETE_POST)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Delete Post", fontSize = 11.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    viewModel.resolveReport(report.id, ModerationAction.BAN_USER)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Ban User", fontSize = 11.sp)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.resolveReport(report.id, ModerationAction.DISMISS)
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Dismiss", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AdminTab.VERIFICATION -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Creator Verification Requests & Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        items(allUsers) { user ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(user.avatarUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = user.displayName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = user.displayName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            VerifiedBadge(status = user.verification, size = 12.dp)
                                        }
                                        Text(
                                            text = "@${user.username} • ${user.followersCount} followers",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val newStatus = if (user.verification == VerificationStatus.NONE) {
                                                VerificationStatus.VERIFIED_CREATOR
                                            } else {
                                                VerificationStatus.NONE
                                            }
                                            viewModel.setUserVerification(user.id, newStatus)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (user.verification != VerificationStatus.NONE) Color.DarkGray else LykoVioletPrimary
                                        )
                                    ) {
                                        Text(
                                            text = if (user.verification != VerificationStatus.NONE) "Revoke" else "Verify",
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                AdminTab.USERS -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(allUsers) { user ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(user.avatarUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = user.displayName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = user.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Role: ${user.role.name} ${if (user.isBlocked) "• [BLOCKED]" else ""}",
                                            fontSize = 11.sp,
                                            color = if (user.isBlocked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.toggleBlockUser(user.id) }
                                    ) {
                                        Icon(
                                            imageVector = if (user.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                                            contentDescription = "Block toggle",
                                            tint = if (user.isBlocked) Color(0xFF10B981) else Color(0xFFEF4444)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                AdminTab.ADS -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Active Ad Campaigns & Monetization",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        items(adCampaigns) { campaign ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = campaign.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Switch(
                                            checked = campaign.isActive,
                                            onCheckedChange = { viewModel.toggleAdCampaign(campaign.id) }
                                        )
                                    }

                                    Text(
                                        text = "Target: ${campaign.targetAudience}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Impressions: ${campaign.impressions}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Clicks: ${campaign.clicks}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Budget: $${campaign.budgetUSD}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LykoCyanTertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}
