package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.ZiyaratData
import com.example.model.ScreenRoute
import com.example.model.ZiyarahItem
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun ZiyaratScreen(
    viewModel: TamaneenaViewModel,
    onNavigate: (ScreenRoute) -> Unit
) {
    var readingZiyarah by remember { mutableStateOf<ZiyarahItem?>(null) }

    if (readingZiyarah != null) {
        ZiyarahReaderView(
            ziyarah = readingZiyarah!!,
            isFavorite = viewModel.isFavorite("ziyarah_${readingZiyarah!!.id}"),
            onFavoriteToggle = { viewModel.toggleFavorite("ziyarah_${readingZiyarah!!.id}") },
            onClose = { readingZiyarah = null }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("ziyarat_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            TamaneenaHeader(
                title = "الزيارات الشريفة",
                onSettingsClick = { onNavigate(ScreenRoute.SETTINGS) },
                onNotificationsClick = { onNavigate(ScreenRoute.SETTINGS) }
            )
        }

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
                            imageVector = Icons.Default.Mosque,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "زيارات أئمة الهدى (عليهم السلام)",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "زيارة النبي وآله الأطهار وسيلة تجديد الولاء ونيل الشفاعة ومغفرة الذنوب.",
                            color = TextGrayMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        items(ZiyaratData.ziyaratList, key = { it.id }) { ziyarah ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { readingZiyarah = ziyarah },
                shape = RoundedCornerShape(18.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NavySurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BrightnessAuto,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = ziyarah.title,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = ziyarah.target,
                                    color = GoldLight,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "قراءة",
                            tint = GoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = ziyarah.description,
                        color = TextGrayMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun ZiyarahReaderView(
    ziyarah: ZiyarahItem,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
    ) {
        TamaneenaHeader(
            title = ziyarah.title,
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = ziyarah.title,
                                color = GoldPrimary,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = ziyarah.target,
                                color = TextGrayMuted,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(onClick = onFavoriteToggle) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "حفظ",
                                tint = if (isFavorite) GoldPrimary else TextGrayMuted
                            )
                        }
                    }

                    if (ziyarah.etiquette.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "آداب الزيارة: ${ziyarah.etiquette}",
                                color = GoldLight,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

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
                        text = ziyarah.text,
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
