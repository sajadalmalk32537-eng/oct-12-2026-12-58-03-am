package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.FiqhKnowledgeBase
import com.example.model.ChatMessage
import com.example.ui.components.TamaneenaHeader
import com.example.ui.theme.*
import com.example.viewmodel.TamaneenaViewModel

@Composable
fun FiqhChatScreen(
    viewModel: TamaneenaViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.fiqhMessages.collectAsState()
    val inputText by viewModel.fiqhInput.collectAsState()
    val isLoading by viewModel.isFiqhLoading.collectAsState()
    val selectedMarja by viewModel.selectedMarja.collectAsState()

    var showMarjaDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalDarkNavy)
            .testTag("fiqh_chat_screen")
    ) {
        TamaneenaHeader(
            title = "المسائل الفقهية (المجيب الذكي)",
            onSettingsClick = {},
            onNotificationsClick = {},
            canGoBack = true,
            onBackClick = onBack
        )

        // Marja Selection Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavyCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "إعدادات المرجع للفتوى:",
                    color = TextGrayMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMarjaDropdown = !showMarjaDropdown },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedMarja,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (showMarjaDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = GoldLight
                    )
                }

                if (showMarjaDropdown) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = NavyStroke)
                    Spacer(modifier = Modifier.height(6.dp))
                    FiqhKnowledgeBase.marjas.forEach { marja ->
                        val isSelected = marja == selectedMarja
                        Text(
                            text = marja,
                            color = if (isSelected) GoldLight else TextWhite,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectedMarja.value = marja
                                    showMarjaDropdown = false
                                }
                                .padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Gemini AI Badge
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            shape = RoundedCornerShape(12.dp),
            color = GoldContainer,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مساعدك الذكي بواسطة Gemini: إجابات شرعية دقيقة مع ذكر المصادر الفقهية المعتمدة طبقاً لمبنى المرجع المختار.",
                    color = GoldLight,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Sample Questions Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FiqhKnowledgeBase.sampleQuestions) { sampleQ ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke),
                    modifier = Modifier.clickable {
                        viewModel.sendFiqhQuestion(sampleQ)
                    }
                ) {
                    Text(
                        text = sampleQ,
                        color = TextWhite,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Chat Message History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatMessageBubble(message = msg)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = GoldPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "جاري استحضار الحكم الشرعي والتحقق من المسألة...",
                            color = GoldLight,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Input Field & Send Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DeepNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyStroke)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.fiqhInput.value = it },
                    placeholder = { Text("اكتب سؤالك الفقهي هنا...", color = TextGrayMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fiqh_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavyStroke,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.sendFiqhQuestion() },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .testTag("fiqh_send_button"),
                    enabled = !isLoading && inputText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "إرسال",
                        tint = RoyalDarkNavy
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 320.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) NavySurfaceElevated else DeepNavyCard
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) GoldBorder.copy(alpha = 0.5f) else NavyStroke
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المجيب الفقهي",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        message.rulingType?.let { ruling ->
                            val badgeColor = when (ruling) {
                                "واجب" -> EmeraldGreen
                                "حرام" -> RubyRed
                                "مستحب" -> GoldLight
                                "جائز" -> CyanAccent
                                else -> TextGrayMuted
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NavySurface
                            ) {
                                Text(
                                    text = ruling,
                                    color = badgeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = message.text,
                    color = TextWhite,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                if (!isUser && message.references != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "المصدر: ${message.references}",
                        color = TextGrayMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
