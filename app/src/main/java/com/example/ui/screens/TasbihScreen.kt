package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun TasbihScreen(
    viewModel: TamaneenaViewModel,
    onNavigate: (ScreenRoute) -> Unit
) {
    val stage by viewModel.tasbihStage.collectAsState()
    val currentCount by viewModel.tasbihCurrentCount.collectAsState()
    val totalCount by viewModel.tasbihTotalCount.collectAsState()
    val completedCycles by viewModel.tasbihCompletedCycles.collectAsState()
    val isCustomMode by viewModel.isCustomTasbihMode.collectAsState()
    val customTarget by viewModel.customTasbihTarget.collectAsState()
    val customTitle by viewModel.customTasbihTitle.collectAsState()

    val targetMax = if (isCustomMode) customTarget else stage.maxCount
    val progress = (currentCount.toFloat() / targetMax.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "tasbih_progress")

    val displayText = if (isCustomMode) customTitle else stage.title

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("tasbih_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TamaneenaHeader(
            title = "المسبحة الإلكترونية",
            onSettingsClick = { onNavigate(ScreenRoute.SETTINGS) },
            onNotificationsClick = { onNavigate(ScreenRoute.SETTINGS) }
        )

        // Mode Switcher: تسبيح الزهراء (ع) / تسبيح حر
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (!isCustomMode) GoldPrimary else Color.Transparent,
                        modifier = Modifier.clickable { viewModel.setFatimaZahraTasbih() }
                    ) {
                        Text(
                            text = "تسبيح فاطمة الزهراء (ع)",
                            color = if (!isCustomMode) RoyalDarkNavy else TextGrayMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCustomMode) GoldPrimary else Color.Transparent,
                        modifier = Modifier.clickable { viewModel.setCustomTasbih("أستغفر الله وأتوب إليه", 100) }
                    ) {
                        Text(
                            text = "تسبيح حر (100x)",
                            color = if (isCustomMode) RoyalDarkNavy else TextGrayMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stages tracker for Zahra mode
        if (!isCustomMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TamaneenaViewModel.TasbihStage.values().forEach { s ->
                    val isActive = stage == s
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = s.title,
                            color = if (isActive) GoldPrimary else TextGrayMuted,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${s.maxCount} مرة",
                            color = if (isActive) GoldLight else TextGrayMuted.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isActive) GoldPrimary else NavyStroke)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Glowing Tasbih Circular Dial (Tap area)
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = GoldLight)
                ) {
                    viewModel.incrementTasbih()
                }
                .testTag("tasbih_counter_circle"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                // Background Track
                drawArc(
                    color = Color(0xFF131A29),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )

                // Foreground Glowing Progress Arc
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(GoldPrimary, CyanAccent, GoldLight, GoldPrimary)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Inner Disc
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(DeepNavyCard)
                    .border(2.dp, GoldBorder.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentCount",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 58.sp
                        ),
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = displayText,
                        color = GoldPrimary,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "اضغط في أي مكان للتسبيح",
                        color = TextGrayMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Action Buttons: (-) Decrement, Reset, (+) Increment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrement
            IconButton(
                onClick = { viewModel.decrementTasbih() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(NavySurface)
                    .border(1.dp, NavyStroke, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "إنقاص",
                    tint = TextWhite,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Reset Button
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NavySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                modifier = Modifier.clickable { viewModel.resetTasbih() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة",
                        tint = GoldLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "إعادة",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Increment
            IconButton(
                onClick = { viewModel.incrementTasbih() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "زيادة",
                    tint = RoyalDarkNavy,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Footer: إجمالي التسبيحات & الدورات
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("إجمالي التسبيحات", color = TextGrayMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$totalCount", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("الدورات المكتملة", color = TextGrayMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$completedCycles", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
