package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrayerAlarmSetting
import com.example.ui.components.AlarmConfigDialog
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun AlarmsScreen(
    viewModel: TamaneenaViewModel,
    onBack: () -> Unit
) {
    val alarms by viewModel.prayerAlarms.collectAsState()
    var selectedAlarmForEdit by remember { mutableStateOf<PrayerAlarmSetting?>(null) }
    var testNotificationSentMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("alarms_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TamaneenaHeader(
                title = "تنبيهات وأذان الصلوات",
                onSettingsClick = {},
                onNotificationsClick = {},
                canGoBack = true,
                onBackClick = onBack
            )

            // Info Card about Background Alarms
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "نظام الأذان يعمل بالخلفية بشكل دقيق حتى في حال قفل الشاشة أو إغلاق التطبيق مع دعم كامل لخيارات الغفوة والاهتزاز.",
                        color = GoldLight,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            if (testNotificationSentMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { testNotificationSentMessage = null }) {
                            Text("حسناً", color = GoldPrimary)
                        }
                    },
                    containerColor = DeepNavyCard,
                    contentColor = TextWhite
                ) {
                    Text(testNotificationSentMessage ?: "")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alarms, key = { it.prayerId }) { alarm ->
                    AlarmItemCard(
                        alarm = alarm,
                        onToggle = { viewModel.toggleAlarm(alarm.prayerId, it) },
                        onEdit = { selectedAlarmForEdit = alarm },
                        onTest = {
                            viewModel.testAlarm(alarm)
                            testNotificationSentMessage = "تم إرسال إشعار أذان صلاة ${alarm.prayerName} للاختبار بنجاح!"
                        }
                    )
                }
            }
        }

        // Edit Dialog
        selectedAlarmForEdit?.let { alarm ->
            AlarmConfigDialog(
                alarm = alarm,
                onDismiss = { selectedAlarmForEdit = null },
                onSave = { updated ->
                    viewModel.updateAlarmSetting(updated)
                    selectedAlarmForEdit = null
                },
                onTestAlarm = { draft ->
                    viewModel.testAlarm(draft)
                    testNotificationSentMessage = "تم اختبار تنبيه صلاة ${draft.prayerName} بنجاح!"
                }
            )
        }
    }
}

@Composable
fun AlarmItemCard(
    alarm: PrayerAlarmSetting,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onEdit() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (alarm.isEnabled) GoldBorder.copy(alpha = 0.6f) else NavyStroke
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (alarm.isEnabled) GoldContainer else NavySurface)
                            .border(1.dp, if (alarm.isEnabled) GoldPrimary else NavyStroke, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = if (alarm.isEnabled) GoldPrimary else TextGrayMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "صلاة ${alarm.prayerName}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = if (alarm.isEnabled) TextWhite else TextGrayMuted
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NavySurface
                            ) {
                                Text(
                                    text = alarm.defaultTime,
                                    color = GoldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = alarm.sound.arabicTitle,
                            color = TextGrayMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RoyalDarkNavy,
                        checkedTrackColor = GoldPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = NavyStroke.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vibration badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (alarm.isVibrationEnabled) EmeraldGreenContainer else NavySurface
                    ) {
                        Text(
                            text = if (alarm.isVibrationEnabled) "اهتزاز مفعل" else "بدون اهتزاز",
                            color = if (alarm.isVibrationEnabled) EmeraldGreen else TextGrayMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    // Snooze badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NavySurface
                    ) {
                        Text(
                            text = "غفوة: ${alarm.snoozeMinutes} دقائق",
                            color = GoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Row {
                    OutlinedButton(
                        onClick = onTest,
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("اختبار", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    TextButton(
                        onClick = onEdit,
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = GoldLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تخصيص", color = GoldLight, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
