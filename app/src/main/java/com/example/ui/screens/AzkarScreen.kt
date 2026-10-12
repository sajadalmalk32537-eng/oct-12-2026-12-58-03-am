package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AzkarData
import com.example.model.ScreenRoute
import com.example.model.ZikrItem
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun AzkarScreen(
    viewModel: TamaneenaViewModel,
    onNavigate: (ScreenRoute) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("الكل") }
    val categories = listOf("الكل", "أذكار الصباح", "أذكار المساء", "أذكار بعد الصلاة", "أذكار النوم")

    val azkarCounts by viewModel.azkarCounts.collectAsState()

    val filteredList = remember(selectedCategory) {
        if (selectedCategory == "الكل") AzkarData.azkarList
        else AzkarData.azkarList.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("azkar_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            TamaneenaHeader(
                title = "الأذكار والتعقيبات",
                onSettingsClick = { onNavigate(ScreenRoute.SETTINGS) },
                onNotificationsClick = { onNavigate(ScreenRoute.SETTINGS) }
            )
        }

        // Category Pills
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

        items(filteredList, key = { it.id }) { zikr ->
            val count = azkarCounts[zikr.id] ?: 0
            val isCompleted = count >= zikr.targetCount

            ZikrCounterCard(
                zikr = zikr,
                currentCount = count,
                isCompleted = isCompleted,
                onTap = { viewModel.incrementZikrCount(zikr.id, zikr.targetCount) },
                onReset = { viewModel.resetZikrCount(zikr.id) }
            )
        }
    }
}

@Composable
fun ZikrCounterCard(
    zikr: ZikrItem,
    currentCount: Int,
    isCompleted: Boolean,
    onTap: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onTap() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) EmeraldGreen else NavyStroke
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = zikr.title,
                    color = GoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onReset, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "تصفير", tint = TextGrayMuted, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCompleted) EmeraldGreenContainer else NavySurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCompleted) EmeraldGreen else GoldBorder)
                    ) {
                        Text(
                            text = "$currentCount / ${zikr.targetCount}",
                            color = if (isCompleted) EmeraldGreen else GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = zikr.text,
                color = TextWhite,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium
            )

            if (zikr.reward.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "الأثر والثواب: ${zikr.reward}",
                    color = TextGrayMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
