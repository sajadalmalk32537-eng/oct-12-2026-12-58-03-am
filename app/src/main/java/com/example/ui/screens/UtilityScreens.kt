package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.DuasData
import com.example.data.QuranData
import com.example.data.ZiyaratData
import com.example.model.ScreenRoute
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun SearchScreen(
    viewModel: TamaneenaViewModel,
    onNavigate: (ScreenRoute) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val matchedSurahs = remember(query) {
        if (query.isBlank()) emptyList()
        else QuranData.surahsList.filter { it.name.contains(query) || it.englishName.contains(query, ignoreCase = true) }
    }

    val matchedDuas = remember(query) {
        if (query.isBlank()) emptyList()
        else DuasData.duasList.filter { it.title.contains(query) || it.text.contains(query) || it.description.contains(query) }
    }

    val matchedZiyarat = remember(query) {
        if (query.isBlank()) emptyList()
        else ZiyaratData.ziyaratList.filter { it.title.contains(query) || it.text.contains(query) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("search_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            TamaneenaHeader(
                title = "البحث الشامل",
                onSettingsClick = { onNavigate(ScreenRoute.SETTINGS) },
                onNotificationsClick = { onNavigate(ScreenRoute.SETTINGS) }
            )
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("ابحث في القرآن، الأدعية، والزيارات...", color = TextGrayMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldLight) },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح", tint = TextGrayMuted)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DeepNavyCard,
                    unfocusedContainerColor = DeepNavyCard,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = NavyStroke,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )
        }

        if (query.isBlank()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NavyStrokeLight, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("اكتب كلمة للبحث في كنوز طمأنينة", color = TextGrayMuted, fontSize = 13.sp)
                    }
                }
            }
        } else {
            if (matchedSurahs.isNotEmpty()) {
                item {
                    Text("السور القرآنية (${matchedSurahs.size})", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(start = 24.dp, top = 16.dp))
                }
                items(matchedSurahs) { s ->
                    SurahListItem(surah = s) {
                        viewModel.selectedSurah.value = s
                        onNavigate(ScreenRoute.SURAH_READER)
                    }
                }
            }

            if (matchedDuas.isNotEmpty()) {
                item {
                    Text("الأدعية المباركة (${matchedDuas.size})", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(start = 24.dp, top = 16.dp))
                }
                items(matchedDuas) { d ->
                    DuaListItemCard(
                        dua = d,
                        isFavorite = viewModel.isFavorite("dua_${d.id}"),
                        onFavoriteToggle = { viewModel.toggleFavorite("dua_${d.id}") },
                        onClick = {
                            viewModel.selectedDua.value = d
                            onNavigate(ScreenRoute.DUA_READER)
                        }
                    )
                }
            }

            if (matchedZiyarat.isNotEmpty()) {
                item {
                    Text("الزيارات (${matchedZiyarat.size})", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(start = 24.dp, top = 16.dp))
                }
                items(matchedZiyarat) { z ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .clickable {
                                viewModel.selectedZiyarah.value = z
                                onNavigate(ScreenRoute.ZIYARAT)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mosque, contentDescription = null, tint = GoldLight)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(z.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: TamaneenaViewModel,
    onNavigate: (ScreenRoute) -> Unit
) {
    val fontSizeMultiplier by viewModel.readerFontSizeMultiplier.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            TamaneenaHeader(
                title = "الإعدادات والمنبهات",
                onSettingsClick = {},
                onNotificationsClick = {}
            )
        }

        // Section: Prayer Alarms Quick Access
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { onNavigate(ScreenRoute.SETTINGS) /* handled via sub navigation if needed */ },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Alarm, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("منظومة منبه وأذان الصلوات", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("أصوات الأذان، الغفوة، والاهتزاز بالخلفية", color = TextGrayMuted, fontSize = 11.sp)
                        }
                    }

                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = GoldLight)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Section: Reader Font Size Adjuster
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("حجم خط القراءة في الأدعية والمصحف", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("خط صغير", color = TextGrayMuted, fontSize = 11.sp)
                        Slider(
                            value = fontSizeMultiplier,
                            onValueChange = { viewModel.readerFontSizeMultiplier.value = it },
                            valueRange = 0.8f..1.5f,
                            modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = GoldPrimary,
                                activeTrackColor = GoldPrimary,
                                inactiveTrackColor = NavySurface
                            )
                        )
                        Text("خط كبير", color = GoldLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Section: Calculation Method Info
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = GoldLight, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("طريقة حساب مواقيت الصلاة", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "المعتمد: فقه أهل البيت (ع) - معهد لواء النجف الأشرف. المغرب بزوال الحمرة المشرقية، والإمساك قبل الفجر باحتياط ١٠ دقائق.",
                        color = TextGrayLight,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Section: App About
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("تطبيق طمأنينة (Tamaneena)", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("الإصدار 1.0 • نسألكم الدعاء", color = TextGrayMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "«أَلا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ»",
                        color = GoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
