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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Verse
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun SurahReaderScreen(
    viewModel: TamaneenaViewModel,
    onBack: () -> Unit
) {
    val surah = viewModel.selectedSurah.collectAsState().value ?: return
    val isPlaying by viewModel.isAudioPlaying.collectAsState()
    val isFavorite = viewModel.isFavorite("surah_${surah.id}")
    var showTafsirDialogForVerse by remember { mutableStateOf<Verse?>(null) }
    val fontSizeMultiplier by viewModel.readerFontSizeMultiplier.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("surah_reader_screen")
    ) {
        TamaneenaHeader(
            title = "سورة ${surah.name}",
            onSettingsClick = {},
            onNotificationsClick = {},
            canGoBack = true,
            onBackClick = onBack
        )

        // Surah Metadata Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleFavorite("surah_${surah.id}") }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "حفظ",
                            tint = if (isFavorite) GoldPrimary else TextGrayMuted
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "سورة ${surah.name}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                        Text(
                            text = "${surah.type} • عدد الآيات: ${surah.ayahCount}",
                            color = TextGrayMuted,
                            fontSize = 11.sp
                        )
                    }

                    // Font zoom buttons
                    Row {
                        IconButton(
                            onClick = {
                                if (fontSizeMultiplier < 1.6f) viewModel.readerFontSizeMultiplier.value += 0.1f
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A+", color = GoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = {
                                if (fontSizeMultiplier > 0.8f) viewModel.readerFontSizeMultiplier.value -= 0.1f
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A-", color = GoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Audio Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.toggleAudio() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "استماع",
                                    tint = RoyalDarkNavy
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isPlaying) "جاري الاستماع للترتيل" else "استمع للتلاوة بصوت عذب",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "القارئ: ميثم التمار",
                                    color = GoldLight,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Basmalah Header (except Surah Tawbah)
        if (surah.id != 9) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    color = GoldLight,
                    fontSize = 20.sp * fontSizeMultiplier,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Verses List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(surah.verses, key = { it.number }) { verse ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTafsirDialogForVerse = verse },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                            ) {
                                Text(
                                    text = "﴿${verse.number}﴾",
                                    color = GoldLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "تفسير",
                                    tint = GoldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "تفسير الآية", color = GoldLight, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = verse.text,
                            color = TextWhite,
                            fontSize = 18.sp * fontSizeMultiplier,
                            lineHeight = 32.sp * fontSizeMultiplier,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Tafsir Dialog
    showTafsirDialogForVerse?.let { v ->
        AlertDialog(
            onDismissRequest = { showTafsirDialogForVerse = null },
            containerColor = DeepNavyCard,
            title = {
                Text(
                    text = "تفسير الآية ﴿${v.number}﴾",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = v.text,
                        color = GoldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = v.tafsir.ifBlank { "تفسير الآية المباركة مستفاد من تفسير الميزان للعلامة الطباطبائي ونور الثقلين." },
                        color = TextWhite,
                        fontSize = 13.sp,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTafsirDialogForVerse = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = RoyalDarkNavy)
                ) {
                    Text("إغلاق")
                }
            }
        )
    }
}
