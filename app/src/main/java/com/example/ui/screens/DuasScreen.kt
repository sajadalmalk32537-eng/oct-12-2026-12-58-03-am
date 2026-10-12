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
import com.example.data.DuasData
import com.example.model.DuaItem
import com.example.model.ScreenRoute
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun DuasScreen(
    viewModel: TamaneenaViewModel,
    onNavigate: (ScreenRoute) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("الكل", "الأدعية المباركة", "الصباح والمساء", "الفرج", "الصحيفة السجادية")

    val filteredDuas = remember(selectedCategory, searchQuery) {
        DuasData.duasList.filter { dua ->
            (selectedCategory == "الكل" || dua.category.contains(selectedCategory)) &&
            (searchQuery.isBlank() || dua.title.contains(searchQuery) || dua.description.contains(searchQuery))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("duas_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            TamaneenaHeader(
                title = "الأدعية المباركة",
                onSettingsClick = { onNavigate(ScreenRoute.SETTINGS) },
                onNotificationsClick = { onNavigate(ScreenRoute.SETTINGS) }
            )
        }

        // Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("ابحث في الأدعية الشريفة...", color = TextGrayMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldLight) },
                shape = RoundedCornerShape(14.dp),
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

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) GoldPrimary else DeepNavyCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GoldLight else NavyStroke
                        ),
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) RoyalDarkNavy else TextWhite,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        items(filteredDuas, key = { it.id }) { dua ->
            DuaListItemCard(
                dua = dua,
                isFavorite = viewModel.isFavorite("dua_${dua.id}"),
                onFavoriteToggle = { viewModel.toggleFavorite("dua_${dua.id}") },
                onClick = {
                    viewModel.selectedDua.value = dua
                    onNavigate(ScreenRoute.DUA_READER)
                }
            )
        }
    }
}

@Composable
fun DuaListItemCard(
    dua: DuaItem,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() },
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NavySurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = dua.title,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = dua.source,
                            color = GoldLight,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "حفظ",
                        tint = if (isFavorite) GoldPrimary else TextGrayMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dua.description,
                color = TextGrayMuted,
                fontSize = 12.sp,
                maxLines = 2,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextGrayMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "الوقت التقريبي: ${dua.durationMinutes} دقيقة",
                        color = TextGrayMuted,
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "قراءة الدعاء", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
