package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MunajatData
import com.example.model.MunajatItem
import com.example.model.ScreenRoute
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun MunajatScreen(
    viewModel: TamaneenaViewModel,
    onNavigate: (ScreenRoute) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("الكل") }
    var readingMunajat by remember { mutableStateOf<MunajatItem?>(null) }
    val categories = listOf("الكل", "المناجاة الكبرى", "المناجاة الصغرى")

    val filteredList = remember(selectedCategory) {
        if (selectedCategory == "الكل") MunajatData.munajatList
        else MunajatData.munajatList.filter { it.category == selectedCategory }
    }

    if (readingMunajat != null) {
        MunajatReaderView(
            munajat = readingMunajat!!,
            onClose = { readingMunajat = null }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("munajat_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            TamaneenaHeader(
                title = "المناجيات الخمس عشرة",
                onSettingsClick = { onNavigate(ScreenRoute.SETTINGS) },
                onNotificationsClick = { onNavigate(ScreenRoute.SETTINGS) }
            )
        }

        // Introduction Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "مناجيات الإمام السجاد (ع)",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "مجموعة من خمس عشرة مناجاة مروية في الصحيفة السجادية لمختلف حالات النفس والروح.",
                            color = TextGrayMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Filter chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSel = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSel) GoldPrimary else DeepNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) GoldLight else NavyStroke),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSel) RoyalDarkNavy else TextWhite,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        items(filteredList, key = { it.id }) { munajat ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { readingMunajat = munajat },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NavySurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${munajat.id}",
                                color = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = munajat.title,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = munajat.description,
                                color = TextGrayMuted,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "قراءة",
                        tint = GoldLight
                    )
                }
            }
        }
    }
}

@Composable
fun MunajatReaderView(
    munajat: MunajatItem,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
    ) {
        TamaneenaHeader(
            title = munajat.title,
            onSettingsClick = {},
            onNotificationsClick = {},
            canGoBack = true,
            onBackClick = onClose
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = munajat.title,
                        color = GoldPrimary,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = munajat.imam,
                        color = TextGrayMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = NavyStroke)
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        color = GoldLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = munajat.text,
                        color = TextWhite,
                        fontSize = 17.sp,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
