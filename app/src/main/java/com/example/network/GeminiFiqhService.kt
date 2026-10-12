package com.example.network

import com.example.BuildConfig
import com.example.data.FiqhKnowledgeBase
import com.example.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiFiqhService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun askFiqhQuestion(question: String, selectedMarja: String): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // If no API key or empty/placeholder, check knowledge base or local rule
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            val localQA = FiqhKnowledgeBase.findQuickAnswer(question)
            return@withContext if (localQA != null) {
                ChatMessage(
                    isUser = false,
                    text = "${localQA.summaryAnswer}\n\n${localQA.detailedAnswer}",
                    marja = selectedMarja,
                    rulingType = localQA.rulingType,
                    references = localQA.reference
                )
            } else {
                ChatMessage(
                    isUser = false,
                    text = "السلام عليكم ورحمة الله وبركاته.\n\nبناءً على مباني الفقه الجعفري وفتاوى $selectedMarja:\n\nمسألتكم الكريمة: \"$question\"\nيُرجى مراجعة المسائل المنتخبة أو منهاج الصالحين (باب العبادات والمعاملات) للتحقق الدقيق من تفاصيل المسألة ومقتضيات الاحتياط الوجوبي أو الاستحبابي.\n\n(ملاحظة: يمكنك تفعيل مفتاح Gemini API في إعدادات التطبيق لتلقي شروح تفصيلية فورية موسعة).",
                    marja = selectedMarja,
                    rulingType = "مستند فقهي",
                    references = "رسالة العملية - $selectedMarja"
                )
            }
        }

        try {
            val systemPrompt = """
                أنت 'المجيب الفقهي الذكي' داخل تطبيق 'طمأنينة' الشيعي الإمامي.
                مهمتك الإجابة على المسائل الشرعية والاستفتاءات وفق مذهب أهل البيت (عليهم السلام) وطبقاً لفتاوى المرجع المختار: $selectedMarja.
                الضوابط:
                1. ابدأ بتحية إسلامية رقيقة (السلام عليكم ورحمة الله وبركاته).
                2. اذكر الحكم الشرعي بوضوح واصطلاح فقهي دقيق (واجب، مستحب، جائز/مباح، مكروه، حرام، احتياط وجوبي).
                3. اشرح علة الحكم أو شروطه وتفريعاته بأسلوب ميسر ومختصر.
                4. اذكر المصدر الفقهي المعتمد للمرجع (مثل: منهاج الصالحين، المسائل المنتخبة، الفتاوى الميسرة).
                5. كن مؤدباً وتجنب التعصب أو الخوض في نقاشات غير فقهية.
            """.trimIndent()

            val rootJson = JSONObject().apply {
                val systemInstObj = JSONObject().apply {
                    val partsArr = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemPrompt) })
                    }
                    put("parts", partsArr)
                }
                put("systemInstruction", systemInstObj)

                val contentsArr = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val userParts = JSONArray().apply {
                            put(JSONObject().apply { put("text", "سؤال فقهي لـ $selectedMarja: $question") })
                        }
                        put("parts", userParts)
                    }
                    put(contentObj)
                }
                put("contents", contentsArr)
            }

            val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful || responseString.isBlank()) {
                val fallback = FiqhKnowledgeBase.findQuickAnswer(question)
                return@withContext ChatMessage(
                    isUser = false,
                    text = fallback?.detailedAnswer
                        ?: "نعتذر عن تعذر الاتصال المؤقت بالخادم. وفق مبنى $selectedMarja يُرجى الرجوع لرسالته العملية في هذه المسألة.",
                    marja = selectedMarja,
                    rulingType = fallback?.rulingType ?: "حكم شرعي",
                    references = fallback?.reference ?: "المسائل المنتخبة"
                )
            }

            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val replyText = parts?.optJSONObject(0)?.optString("text")
                ?: "لم يتم استلام نص الإجابة."

            // Infer ruling tag if present in text
            val rulingType = when {
                replyText.contains("واجب") || replyText.contains("يجب") -> "واجب"
                replyText.contains("حرام") || replyText.contains("يحرم") -> "حرام"
                replyText.contains("مستحب") || replyText.contains("يستحب") -> "مستحب"
                replyText.contains("مكروه") || replyText.contains("يكره") -> "مكروه"
                replyText.contains("جائز") || replyText.contains("يجوز") -> "جائز"
                else -> "بيان شرعي"
            }

            ChatMessage(
                isUser = false,
                text = replyText,
                marja = selectedMarja,
                rulingType = rulingType,
                references = "استفتاء مؤيّد وفق مبنى $selectedMarja"
            )
        } catch (e: Exception) {
            val fallback = FiqhKnowledgeBase.findQuickAnswer(question)
            ChatMessage(
                isUser = false,
                text = fallback?.detailedAnswer
                    ?: "عذراً، حدث خطأ أثناء معالجة السؤال. طبقاً لمبنى $selectedMarja، ننصح بالرجوع لموقع المرجعية أو وكيل المرجع المعتمد.",
                marja = selectedMarja,
                rulingType = fallback?.rulingType ?: "إرشاد فقهي",
                references = fallback?.reference ?: "منهاج الصالحين"
            )
        }
    }
}
