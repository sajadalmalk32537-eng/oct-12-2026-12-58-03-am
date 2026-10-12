package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlarmSound
import com.example.model.PrayerAlarmSetting
import com.example.model.PrayerTime
import com.example.model.ScreenRoute
import com.example.ui.theme.*

@Composable
fun TamaneenaHeader(
    title: String,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    canGoBack: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canGoBack) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NavySurface)
                    .testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = GoldLight
                )
            }
        } else {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NavySurface)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات",
                    tint = TextGrayLight
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = GoldPrimary,
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(NavySurface)
                .testTag("notifications_button")
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "تنبيهات الصلاة",
                tint = GoldLight
            )
        }
    }
}

@Composable
fun TamaneenaBottomBar(
    currentScreen: ScreenRoute,
    onNavigate: (ScreenRoute) -> Unit
) {
    NavigationBar(
        containerColor = DeepNavyCard,
        tonalElevation = 8.dp,
        modifier = Modifier.border(
            width = 1.dp,
            color = NavyStroke,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        )
    ) {
        val items = listOf(
            Triple(ScreenRoute.HOME, "الرئيسية", Icons.Default.Home),
            Triple(ScreenRoute.QURAN, "القرآن", Icons.Default.MenuBook),
            Triple(ScreenRoute.DUAS, "الأدعية", Icons.Default.AutoStories),
            Triple(ScreenRoute.SEARCH, "البحث", Icons.Default.Search),
            Triple(ScreenRoute.SETTINGS, "الإعدادات", Icons.Default.Settings)
        )

        items.forEach { (route, label, icon) ->
            val isSelected = currentScreen == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) GoldPrimary else TextGrayMuted
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (isSelected) GoldPrimary else TextGrayMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = GoldContainer
                )
            )
        }
    }
}

@Composable
fun PrayerTimesCard(
    location: String,
    hijriDate: String,
    gregorianDate: String,
    prayers: List<PrayerTime>,
    onOpenAlarms: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onOpenAlarms() }
            .testTag("prayer_times_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NavySurfaceElevated.copy(alpha = 0.8f),
                            DeepNavyCard
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            color = TextWhite,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                        modifier = Modifier.clickable { onOpenAlarms() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ضبط المنبه",
                                color = GoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$hijriDate • $gregorianDate",
                    color = TextGrayMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    prayers.forEach { prayer ->
                        val isHighlight = prayer.isHighlighted
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isHighlight) GoldPrimary else NavySurface
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isHighlight) GoldLight else NavyStroke,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (prayer.id == "sunrise") Icons.Default.WbSunny else Icons.Default.Brightness2,
                                    contentDescription = null,
                                    tint = if (isHighlight) RoyalDarkNavy else GoldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = prayer.arabicName,
                                color = if (isHighlight) GoldLight else TextWhite,
                                fontSize = 12.sp,
                                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium
                            )

                            Text(
                                text = prayer.timeFormatted,
                                color = if (isHighlight) GoldPrimary else TextGrayMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickModuleGridItem(
    title: String,
    icon: ImageVector,
    iconTint: Color = GoldPrimary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(NavySurface)
                    .border(1.dp, NavyStrokeLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AlarmConfigDialog(
    alarm: PrayerAlarmSetting,
    onDismiss: () -> Unit,
    onSave: (PrayerAlarmSetting) -> Unit,
    onTestAlarm: (PrayerAlarmSetting) -> Unit
) {
    var isEnabled by remember { mutableStateOf(alarm.isEnabled) }
    var selectedSound by remember { mutableStateOf(alarm.sound) }
    var isVibrate by remember { mutableStateOf(alarm.isVibrationEnabled) }
    var snoozeMinutes by remember { mutableStateOf(alarm.snoozeMinutes) }
    var preAlertMinutes by remember { mutableStateOf(alarm.preAlertMinutes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavyCard,
        titleContentColor = GoldPrimary,
        textContentColor = TextWhite,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تخصيص أذان صلاة ${alarm.prayerName}")
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RoyalDarkNavy,
                        checkedTrackColor = GoldPrimary
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "وقت الأذان: ${alarm.defaultTime}",
                    color = GoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                HorizontalDivider(color = NavyStroke)

                // Sound Selection
                Text("نغمة / صوت الأذان:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AlarmSound.values().forEach { soundOption ->
                        val isSelected = selectedSound == soundOption
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoldContainer else NavySurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) GoldPrimary else NavyStroke
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSound = soundOption }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedSound = soundOption },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = soundOption.arabicTitle,
                                        fontSize = 12.sp,
                                        color = if (isSelected) GoldLight else TextWhite,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = soundOption.description,
                                        fontSize = 10.sp,
                                        color = TextGrayMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // Vibration Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("الاهتزاز عند التنبيه", color = TextWhite, fontSize = 13.sp)
                    Checkbox(
                        checked = isVibrate,
                        onCheckedChange = { isVibrate = it },
                        colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = RoyalDarkNavy)
                    )
                }

                // Snooze options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("مدة الغفوة (Snooze):", color = TextWhite, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(5, 10, 15).forEach { mins ->
                            val isSel = snoozeMinutes == mins
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) GoldPrimary else NavySurface,
                                modifier = Modifier.clickable { snoozeMinutes = mins }
                            ) {
                                Text(
                                    text = "$mins د",
                                    color = if (isSel) RoyalDarkNavy else TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Pre-Alert options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("التنبيه المسبق:", color = TextWhite, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0 to "بالوقت", 5 to "قبل 5د", 15 to "قبل 15د").forEach { (mins, label) ->
                            val isSel = preAlertMinutes == mins
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) GoldPrimary else NavySurface,
                                modifier = Modifier.clickable { preAlertMinutes = mins }
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) RoyalDarkNavy else TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Test Alarm Button
                OutlinedButton(
                    onClick = {
                        val currentDraft = alarm.copy(
                            isEnabled = isEnabled,
                            sound = selectedSound,
                            isVibrationEnabled = isVibrate,
                            snoozeMinutes = snoozeMinutes,
                            preAlertMinutes = preAlertMinutes
                        )
                        onTestAlarm(currentDraft)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("اختبار التنبيه الآن في الخلفية", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        alarm.copy(
                            isEnabled = isEnabled,
                            sound = selectedSound,
                            isVibrationEnabled = isVibrate,
                            snoozeMinutes = snoozeMinutes,
                            preAlertMinutes = preAlertMinutes
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = RoyalDarkNavy)
            ) {
                Text("حفظ الإعدادات", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextGrayMuted)
            }
        }
    )
}
