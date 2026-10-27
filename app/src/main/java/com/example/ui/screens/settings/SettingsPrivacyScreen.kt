package com.example.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VerificationStatus
import com.example.ui.theme.LykoCyanTertiary
import com.example.ui.theme.LykoVioletPrimary
import com.example.ui.viewmodel.LykoViewModel
import com.example.ui.viewmodel.NavigationDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPrivacyScreen(
    viewModel: LykoViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var isPrivateAccount by remember { mutableStateOf(false) }
    var allowDirectMessagesFromEveryone by remember { mutableStateOf(true) }
    var filterOffensiveComments by remember { mutableStateOf(true) }
    var highQualityMediaUploads by remember { mutableStateOf(true) }
    var showBlockedAccountsDialog by remember { mutableStateOf(false) }
    var showVerificationRequestDialog by remember { mutableStateOf(false) }

    val blockedUsers = remember(allUsers) { allUsers.filter { it.isBlocked } }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & Privacy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(NavigationDestination.PROFILE) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Security & Encryption Section
            item {
                SettingsSectionHeader(title = "Security & Encryption")
                ListItem(
                    headlineContent = { Text("End-to-End Encryption (E2EE)") },
                    supportingContent = { Text("All direct chats and voice notes are cryptographically secured.") },
                    leadingContent = { Icon(Icons.Default.Lock, contentDescription = null, tint = LykoCyanTertiary) },
                    trailingContent = {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LykoCyanTertiary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = LykoCyanTertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            // Privacy Controls
            item {
                SettingsSectionHeader(title = "Privacy & Safety")

                ListItem(
                    headlineContent = { Text("Private Account") },
                    supportingContent = { Text("Only approved followers can view your posts and stories.") },
                    leadingContent = { Icon(Icons.Outlined.Shield, contentDescription = null) },
                    trailingContent = {
                        Switch(
                            checked = isPrivateAccount,
                            onCheckedChange = {
                                isPrivateAccount = it
                                viewModel.showMessage(if (it) "Account switched to Private" else "Account switched to Public")
                            }
                        )
                    }
                )

                ListItem(
                    headlineContent = { Text("Direct Message Requests") },
                    supportingContent = { Text("Allow message requests from anyone") },
                    leadingContent = { Icon(Icons.Outlined.Message, contentDescription = null) },
                    trailingContent = {
                        Switch(
                            checked = allowDirectMessagesFromEveryone,
                            onCheckedChange = { allowDirectMessagesFromEveryone = it }
                        )
                    }
                )

                ListItem(
                    headlineContent = { Text("AI Anti-Spam & Comment Filter") },
                    supportingContent = { Text("Automatically hide offensive words and spam comments") },
                    leadingContent = { Icon(Icons.Outlined.FilterAlt, contentDescription = null) },
                    trailingContent = {
                        Switch(
                            checked = filterOffensiveComments,
                            onCheckedChange = { filterOffensiveComments = it }
                        )
                    }
                )

                ListItem(
                    headlineContent = { Text("Blocked Accounts (${blockedUsers.size})") },
                    supportingContent = { Text("Manage users you have blocked") },
                    leadingContent = { Icon(Icons.Outlined.Block, contentDescription = null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { showBlockedAccountsDialog = true }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            // Creator & Verification
            item {
                SettingsSectionHeader(title = "Creator & Verification")

                ListItem(
                    headlineContent = { Text("Request Verification Badge") },
                    supportingContent = {
                        Text(
                            when (currentUser.verification) {
                                VerificationStatus.VERIFIED_CREATOR -> "Current status: Verified Creator ✨"
                                VerificationStatus.VERIFIED_OFFICIAL -> "Current status: Official Account 🔵"
                                VerificationStatus.VERIFIED_ADMIN -> "Current status: Platform Administrator 🛡️"
                                VerificationStatus.NONE -> "Apply for creator or official verification badge"
                            }
                        )
                    },
                    leadingContent = { Icon(Icons.Default.Verified, contentDescription = null, tint = LykoVioletPrimary) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { showVerificationRequestDialog = true }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            // Media & App Experience
            item {
                SettingsSectionHeader(title = "Preferences & Data")

                ListItem(
                    headlineContent = { Text("Upload in High Quality") },
                    supportingContent = { Text("Upload photos and 4K reels with maximum visual fidelity") },
                    leadingContent = { Icon(Icons.Outlined.Hd, contentDescription = null) },
                    trailingContent = {
                        Switch(
                            checked = highQualityMediaUploads,
                            onCheckedChange = { highQualityMediaUploads = it }
                        )
                    }
                )

                ListItem(
                    headlineContent = { Text("Clear Local Cache") },
                    supportingContent = { Text("Free up storage (currently 14.2 MB)") },
                    leadingContent = { Icon(Icons.Outlined.CleaningServices, contentDescription = null) },
                    modifier = Modifier.clickable { viewModel.showMessage("Cache cleared successfully!") }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            // About & Legal
            item {
                SettingsSectionHeader(title = "About Lyko")
                ListItem(
                    headlineContent = { Text("Lyko Social App") },
                    supportingContent = { Text("Version 2.4.0 (2026 Edition)") },
                    leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) }
                )

                ListItem(
                    headlineContent = { Text("Log Out", color = MaterialTheme.colorScheme.error) },
                    leadingContent = { Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable { viewModel.showMessage("Logged out") }
                )
            }
        }
    }

    // Blocked Accounts Dialog
    if (showBlockedAccountsDialog) {
        AlertDialog(
            onDismissRequest = { showBlockedAccountsDialog = false },
            title = { Text("Blocked Accounts", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (blockedUsers.isEmpty()) {
                        Text("You haven't blocked any accounts.", fontSize = 13.sp)
                    } else {
                        blockedUsers.forEach { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("@${user.username}", fontWeight = FontWeight.Bold)
                                TextButton(onClick = { viewModel.toggleBlockUser(user.id) }) {
                                    Text("Unblock", color = Color(0xFF10B981))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBlockedAccountsDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Verification Request Dialog
    if (showVerificationRequestDialog) {
        var linksInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showVerificationRequestDialog = false },
            title = { Text("Apply for Creator Verification", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "To get the Lyko Verified Creator badge, share links to your portfolio, music channels, or creative profiles:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = linksInput,
                        onValueChange = { linksInput = it },
                        placeholder = { Text("https://soundcloud.com/..., https://artstation.com/...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setUserVerification(currentUser.id, VerificationStatus.VERIFIED_CREATOR)
                        viewModel.showMessage("Verification badge awarded to your account! 🎉")
                        showVerificationRequestDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LykoVioletPrimary)
                ) {
                    Text("Submit Application")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVerificationRequestDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = LykoVioletPrimary,
        modifier = Modifier.padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 4.dp)
    )
}
