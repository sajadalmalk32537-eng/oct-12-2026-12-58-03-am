package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PrayerTimesData
import com.example.model.ScreenRoute
import com.example.ui.components.PrayerTimesCard
import com.example.ui.components.QuickModuleGridItem
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun HomeScreen(
    viewModel: TamaneenaViewModel,
    onNavigate: (ScreenRoute) -> Unit
) {
    val alarms by viewModel.prayerAlarms.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            TamaneenaHeader(
                title = "طمأنينة",
                onSettingsClick = { onNavigate(ScreenRoute.SETTINGS) },
                onNotificationsClick = { onNavigate(ScreenRoute.SETTINGS) }
            )
        }

        // Prayer Times Widget
        item {
            PrayerTimesCard(
                location = PrayerTimesData.locationName,
                hijriDate = PrayerTimesData.hijriDate,
                gregorianDate = PrayerTimesData.gregorianDate,
                prayers = PrayerTimesData.defaultPrayers,
                onOpenAlarms = { onNavigate(ScreenRoute.SETTINGS) }
            )
        }

        item { Spacer(modifier = Modifier.height(22.dp)) }

        // Quick Access Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        QuickModuleGridItem(
                            title = "المناجاة",
                            icon = Icons.Default.NightsStay,
                            iconTint = GoldPrimary,
                            onClick = { onNavigate(ScreenRoute.MUNAJAT) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        QuickModuleGridItem(
                            title = "الأدعية",
                            icon = Icons.Default.VolunteerActivism,
                            iconTint = GoldLight,
                            onClick = { onNavigate(ScreenRoute.DUAS) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        QuickModuleGridItem(
                            title = "القرآن الكريم",
                            icon = Icons.Default.MenuBook,
                            iconTint = GoldLighter,
                            onClick = { onNavigate(ScreenRoute.QURAN) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        QuickModuleGridItem(
                            title = "المسبحة",
                            icon = Icons.Default.Timelapse,
                            iconTint = GoldPrimary,
                            onClick = { onNavigate(ScreenRoute.TASBIH) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        QuickModuleGridItem(
                            title = "الأذكار",
                            icon = Icons.Default.WbTwilight,
                            iconTint = EmeraldGreen,
                            onClick = { onNavigate(ScreenRoute.AZKAR) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        QuickModuleGridItem(
                            title = "الزيارات",
                            icon = Icons.Default.Mosque,
                            iconTint = GoldLight,
                            onClick = { onNavigate(ScreenRoute.ZIYARAT) }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Featured: صلاة الليل & السيرة والفضائل
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // صلاة الليل Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clickable { onNavigate(ScreenRoute.SALAT_LAYL) }
                        .testTag("salat_layl_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.salat_layl_banner),
                            contentDescription = "صلاة الليل",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            alpha = 0.35f
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, DeepNavyCard.copy(alpha = 0.85f))
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GoldContainer)
                                    .border(1.dp, GoldBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Brightness3,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "صلاة الليل",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "طريق القرب من الله",
                                    color = GoldLight,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // السيرة والفضائل Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clickable { onNavigate(ScreenRoute.AHLULBAYT) }
                        .testTag("ahlulbayt_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.tamaneena_logo),
                            contentDescription = "السيرة والفضائل",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            alpha = 0.25f
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, DeepNavyCard.copy(alpha = 0.85f))
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(NavySurfaceElevated)
                                    .border(1.dp, NavyStrokeLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "السيرة والفضائل",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "نور للحياة",
                                    color = TextGrayMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(18.dp)) }

        // Smart Fiqh Banner (المجيب الذكي)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onNavigate(ScreenRoute.FIQH_CHAT) }
                    .testTag("fiqh_ai_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "المجيب الفقهي الذكي",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldContainer
                                ) {
                                    Text(
                                        text = "Gemini AI",
                                        color = GoldLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "إجابات فورية موثقة وفق مباني مراجع التقليد (ع)",
                                color = TextGrayMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "دخول",
                        tint = GoldLight
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(18.dp)) }

        // Customizable Prayer Alarms Showcase Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessAlarm,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "منظومة منبه الصلوات المخصصة",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        TextButton(onClick = { onNavigate(ScreenRoute.SETTINGS) }) {
                            Text("تعديل الكل", color = GoldLight, fontSize = 12.sp)
                        }
                    }

                    Text(
                        text = "يعمل التنبيه في الخلفية حتى عند إغلاق التطبيق مع خيارات الأذان والغفوة والاهتزاز.",
                        color = TextGrayMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    alarms.take(3).forEach { alarm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "صلاة ${alarm.prayerName} (${alarm.defaultTime})",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${alarm.sound.arabicTitle} • غفوة ${alarm.snoozeMinutes} د",
                                    color = GoldLight,
                                    fontSize = 10.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.testAlarm(alarm) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = "اختبار",
                                        tint = GoldLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Switch(
                                    checked = alarm.isEnabled,
                                    onCheckedChange = { viewModel.toggleAlarm(alarm.prayerId, it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = RoyalDarkNavy,
                                        checkedTrackColor = GoldPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
